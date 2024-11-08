package dbdr.domain.core.ocr.controller;

import java.net.URL;

import dbdr.domain.core.ocr.service.OcrService;
import dbdr.domain.core.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OcrController {

	private final OcrService ocrService;
	private final S3Service s3Service;

	/**
	 * OCR 텍스트 추출 API
	 * @param objectKey S3에 저장된 이미지의 objectKey
	 * @param isTable 표 추출 모드 여부 (true: 표 추출, false: 일반 텍스트 추출)
	 * @return OCR 결과 텍스트
	 */
	@GetMapping("/perform-ocr")
	public Mono<ResponseEntity<String>> performOcr(
		@RequestParam String objectKey,
		@RequestParam(defaultValue = "false") boolean isTable) {
		try {
			// S3에서 이미지 URL 가져오기
			URL imageUrl = s3Service.getS3FileUrl(objectKey);

			// 클로바 OCR API 호출하여 텍스트 추출
			return ocrService.performOcr(imageUrl, objectKey, isTable)
				.map(result -> ResponseEntity.ok(result)) // 성공 시 OCR 결과 반환
				.onErrorResume(e -> {
					log.error("OCR 실패: {}", e.getMessage());
					return Mono.just(ResponseEntity.internalServerError().body("OCR 요청 실패: " + e.getMessage()));
				});
		} catch (Exception e) {
			log.error("이미지 URL 가져오기 실패: {}", e.getMessage());
			return Mono.just(ResponseEntity.internalServerError().body("이미지 URL 가져오기 실패: " + e.getMessage()));
		}
	}
}
