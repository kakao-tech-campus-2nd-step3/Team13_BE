package dbdr.domain.core.ocr.controller;

import java.net.URL;

import dbdr.domain.core.ocr.service.OcrService;
import dbdr.domain.core.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "[요양보호사] OCR로 차트 작성", description = "Naver Clova OCR을 통해 표 이미지에서 텍스트 추출하기")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/ocr/chart")
public class OcrController {

	private final OcrService ocrService;
	private final S3Service s3Service;

	@Operation(summary = "OCR 수행 후 추출된 텍스트 반환", description = "S3에 업로드된 표 이미지를 가지고 OCR을 수행합니다.")
	@GetMapping("/perform")
	public ResponseEntity<String> performOcr(@RequestParam String objectKey) {
		try {
			// S3에서 이미지 URL 가져오기
			URL imageUrl = s3Service.getS3FileUrl(objectKey);

			// 클로바 OCR API 호출하여 텍스트 추출
			return ocrService.performOcr(imageUrl, objectKey)
				.map(result -> ResponseEntity.ok(result)) // 성공 시 OCR 결과 반환
				.block(); // Mono를 동기식으로 반환하여 ResponseEntity<String> 타입을 유지
		} catch (Exception e) {
			log.error("이미지 URL 가져오기 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().body("이미지 URL 가져오기 실패: " + e.getMessage());
		}
	}
}
