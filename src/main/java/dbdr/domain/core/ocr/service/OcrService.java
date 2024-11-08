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
	public Mono<String> performOcr(URL imageUrl, String objectKey, boolean isTable) {
		return sendOcrRequest(imageUrl, isTable)
			.flatMap(response -> {
				String extractedText = isTable ? extractTableText(response) : extractPlainText(response);
				saveOrUpdateOcrData(imageUrl, objectKey, extractedText);
				return Mono.just(extractedText);
			})
			.doOnError(error -> log.error("OCR 요청 실패: {}", error.getMessage()))
			.onErrorResume(WebClientResponseException.class, ex -> Mono.error(new RuntimeException("클로바 OCR 요청 실패: " + ex.getMessage())));
	}

	// 클로바 OCR API에 요청을 보내는 메서드
	private Mono<String> sendOcrRequest(URL imageUrl, boolean isTable) {
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
						"type", isTable ? "TABLE" : "DOCUMENT" // TABLE 또는 DOCUMENT 설정
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
			JsonNode tables = root.path("images").get(0).path("tables");

			for (JsonNode table : tables) {
				JsonNode cells = table.path("cells");
				for (JsonNode cell : cells) {
					String inferText = cell.path("inferText").asText();
					tableText.append(inferText).append(" | "); // 셀 구분을 위해 '|' 추가
				}
				tableText.append("\n"); // 행 구분을 위해 줄바꿈 추가
			}
		} catch (Exception e) {
			log.error("표 데이터 추출 중 오류 발생: {}", e.getMessage());
		}
		return tableText.toString().trim();
	}

	// JSON 응답에서 일반 텍스트를 추출하는 메서드
	private String extractPlainText(String response) {
		StringBuilder plainTextResult = new StringBuilder();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response);
			JsonNode fields = root.path("images").get(0).path("fields");

			for (JsonNode field : fields) {
				String inferText = field.path("inferText").asText();
				plainTextResult.append(inferText).append(" ");
			}
		} catch (Exception e) {
			log.error("일반 텍스트 추출 중 오류 발생: {}", e.getMessage());
		}
		return plainTextResult.toString().trim();
	}

	// OCR 데이터 저장 또는 업데이트 메서드
	@Transactional
	public void saveOrUpdateOcrData(URL imageUrl, String objectKey, String ocrResult) {
		OcrData ocrData = ocrRepository.findByObjectKey(objectKey);

		if (ocrData != null) {
			// 기존 데이터가 있으면 ocrResult 업데이트
			ocrData.setOcrResult(ocrResult);
			log.info("기존 OCR 데이터 업데이트: {}", ocrData);
		} else {
			// 새로운 데이터를 생성
			ocrData = new OcrData();
			ocrData.setUrl(imageUrl.toString());
			ocrData.setObjectKey(objectKey);
			ocrData.setOcrResult(ocrResult);
			log.info("새로운 OCR 데이터 저장: {}", ocrData);
		}

		ocrRepository.save(ocrData);
	}
}
