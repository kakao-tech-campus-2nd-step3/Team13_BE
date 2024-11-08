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

	// OCR 데이터 저장 메서드
	@Transactional
	public void saveOcrData(URL imageUrl, String objectKey, String ocrResult) {
		OcrData existingData = ocrRepository.findByObjectKey(objectKey);

		if (existingData != null) {
			existingData.setOcrResult(ocrResult);
			ocrRepository.save(existingData);
		} else {
			OcrData newData = new OcrData();
			newData.setUrl(imageUrl.toString());
			newData.setObjectKey(objectKey);
			newData.setOcrResult(ocrResult);
			ocrRepository.save(newData);
		}
	}

	// OCR 텍스트 추출 메서드
	public Mono<String> performOcr(URL imageUrl, String objectKey) {
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
						"url", imageUrl.toString()
					)
				}
			))
			.retrieve()
			.bodyToMono(String.class)
			.flatMap(response -> {
				// OCR 응답에서 inferText만 추출
				String inferTextResult = extractInferText(response);

				// URL, objectKey와 함께 DB에 저장
				saveOcrData(imageUrl, objectKey, inferTextResult);

				return Mono.just(inferTextResult); // 추출한 텍스트 반환
			})
			.doOnError(error -> log.error("OCR 요청 실패: {}", error.getMessage()))
			.onErrorResume(WebClientResponseException.class, ex -> Mono.error(new RuntimeException("클로바 OCR 요청 실패: " + ex.getMessage())));
	}

	// inferText 추출 메서드
	private String extractInferText(String response) {
		StringBuilder inferTextResult = new StringBuilder();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response);
			JsonNode fields = root.path("images").get(0).path("fields");

			for (JsonNode field : fields) {
				String inferText = field.path("inferText").asText();
				inferTextResult.append(inferText).append(" ");
			}
		} catch (Exception e) {
			log.error("inferText 추출 중 오류 발생: {}", e.getMessage());
		}
		return inferTextResult.toString().trim();
	}
}
