package dbdr.domain.core.s3.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dbdr.domain.core.s3.service.S3Service;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
	private final S3Service s3Service;

	// Presigned URL 생성 API
	@GetMapping("/generate-presigned-url")
	public String generatePresignedUrl(@RequestParam String objectKey) {
		URL presignedUrl = s3Service.generatePresignedUrl(objectKey);
		return presignedUrl.toString();
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
