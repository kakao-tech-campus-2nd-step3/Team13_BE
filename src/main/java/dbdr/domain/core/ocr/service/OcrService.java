package dbdr.domain.core.ocr.service;

import dbdr.domain.core.ocr.entity.OcrData;
import dbdr.domain.core.ocr.repository.OcrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrService {
	private final OcrRepository ocrRepository;
	private final WebClient webClient = WebClient.builder().build();

	@Value("${clova-ocr.api-url}")
	private String apiUrl;

	@Value("${clova-ocr.secret-key}")
	private String secretKey;

	// OCR 요청 메서드
	@Transactional
	public Mono<String> performOcr(URL imageUrl, String objectKey) {
		return sendOcrRequest(imageUrl)
			.flatMap(response -> {
				String extractedText = extractTableText(response);
				updateOcrData(objectKey, extractedText);
				return Mono.just(extractedText);
			})
			.doOnError(error -> log.error("OCR 요청 실패: {}", error.getMessage()))
			.onErrorResume(WebClientResponseException.class, ex -> Mono.error(new RuntimeException("클로바 OCR 요청 실패: " + ex.getMessage())));
	}

	// 클로바 OCR API에 요청을 보내는 메서드 (TABLE 타입으로 고정)
	private Mono<String> sendOcrRequest(URL imageUrl) {
		return webClient.post()
			.uri(apiUrl)
			.header("X-OCR-SECRET", secretKey)
			.bodyValue(Map.of(
				"version", "V2",
				"requestId", "unique-request-id",
				"timestamp", System.currentTimeMillis(),
				"images", new Object[]{
					Map.of(
						"format", "jpg",
						"name", "sample",
						"url", imageUrl.toString(),
						"type", "TABLE" // 항상 TABLE 타입으로 설정
					)
				}
			))
			.retrieve()
			.bodyToMono(String.class);
	}

	// JSON 응답에서 표 데이터를 추출하는 메서드
	private String extractTableText(String response) {
		StringBuilder tableText = new StringBuilder();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response);
			JsonNode fields = root.path("images").get(0).path("fields");

			if (fields.isMissingNode() || fields.isEmpty()) {
				log.warn("OCR 응답에 'fields' 데이터가 없습니다.");
				return "데이터가 없습니다.";
			}

			for (JsonNode field : fields) {
				String inferText = field.path("inferText").asText();
				tableText.append(inferText).append(" "); // 텍스트 조각을 공백으로 구분하여 추가
			}
		} catch (Exception e) {
			log.error("데이터 추출 중 오류 발생: {}", e.getMessage());
		}
		return tableText.toString().trim();
	}

	// OCR 데이터 저장
	@Transactional
	public void createOcrDate(String objectKey) {
		OcrData ocrData = new OcrData();
		ocrData.setObjectKey(objectKey);
		ocrRepository.save(ocrData);
		log.info("새로운 OCR 데이터 저장: {}", ocrData);
	}

	// OCR 데이터 업데이트
	@Transactional
	public void updateOcrData(String objectKey, String ocrResult) {
		OcrData ocrData = ocrRepository.findByObjectKey(objectKey);
		ocrData.setOcrResult(ocrResult);
		ocrRepository.save(ocrData);
		log.info("기존 OCR 데이터 업데이트: {}", ocrData);
	}
}
