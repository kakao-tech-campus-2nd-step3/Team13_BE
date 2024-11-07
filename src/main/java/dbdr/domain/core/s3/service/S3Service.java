package dbdr.domain.core.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@Service
public class S3Service {
	private final AmazonS3 amazonS3;
	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;

	public S3Service(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}

	// Presigned URL 생성 메서드
	public URL generatePresignedUrl(String objectKey) {
		// 만료 시간을 2분(120초)로 고정
		Date expiration = new Date(System.currentTimeMillis() + 120 * 1000);

		// Presigned URL 요청 생성
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(bucketName, objectKey)
				.withMethod(HttpMethod.PUT)
				.withExpiration(expiration);

		// Presigned URL 생성
		return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
	}

	// test : Presigned URL을 이용해 S3에 파일 업로드 테스트 메서드
	public void uploadFileToS3(String objectKey, File file) {
		try {
			// Presigned URL 생성
			URL presignedUrl = generatePresignedUrl(objectKey);

			// HttpURLConnection을 사용해 파일 업로드
			HttpURLConnection connection = (HttpURLConnection) presignedUrl.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "image/jpeg"); // 파일 타입 설정

			// 파일을 Presigned URL로 전송
			try (OutputStream outputStream = connection.getOutputStream();
				 FileInputStream inputStream = new FileInputStream(file)) {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
			}

			// 응답 확인
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("파일이 성공적으로 S3에 업로드되었습니다.");
			} else {
				System.out.println("파일 업로드 실패. 응답 코드: " + responseCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("S3 업로드 중 오류가 발생했습니다.");
		}
	}
}
