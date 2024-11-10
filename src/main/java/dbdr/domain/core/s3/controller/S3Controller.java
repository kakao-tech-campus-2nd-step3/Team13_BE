package dbdr.domain.core.s3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dbdr.domain.core.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Tag(name = "[프론트엔드] Presgined URL", description = "이미지 업로드를 위한 Presigned URL 생성 및 이미지 URL 저장하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/s3/chart")
public class S3Controller {
	private final S3Service s3Service;

	// Presigned URL 생성 API
	@Operation(summary = "Presigned URL 생성", description = "S3에 이미지 업로드를 위한 Presigned URL 생성")
	@GetMapping("/generate-url")
	public ResponseEntity<String> generatePresignedUrl(@RequestParam String objectKey) {
		URL presignedUrl = s3Service.generatePresignedUrl(objectKey);
		return ResponseEntity.status(HttpStatus.OK).body(presignedUrl.toString());
	}

	// 프론트엔드 쪽에서 이미지 업로드 완료 후 키 값을 주면 DB에 저장하는 API
	@Operation(summary = "이미지 URL DB에 저장", description = "S3에 업로드된 이미지 URL을 DB에 저장")
	@PostMapping("/save-image-url")
	public ResponseEntity<String> saveImageUrl(@RequestParam String objectKey) {
		try {
			// S3에서 이미지 URL 가져오기
			URL imageUrl = s3Service.getS3FileUrl(objectKey);
			// DB에 URL과 objectKey 저장
			s3Service.saveImageUrlToDatabase(objectKey);
			return ResponseEntity.ok("이미지 URL 저장 완료하였습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 URL 저장 중 오류가 발생했습니다.");
		}
	}

	// test : Presigned URL을 이용한 파일 업로드 테스트 API
	@PostMapping("/test-upload")
	public String testUpload(@RequestParam String objectKey, @RequestParam("file") MultipartFile multipartFile) {
		// MultipartFile을 File 객체로 변환
		File file = convertMultipartFileToFile(multipartFile);
		if (file == null) {
			return "파일 변환에 실패했습니다.";
		}

		// S3에 업로드
		s3Service.uploadFileToS3(objectKey, file);

		// 임시 파일 삭제
		file.delete();

		return "테스트 업로드 완료!";
	}

	private File convertMultipartFileToFile(MultipartFile file) {
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return convFile;
	}
}
