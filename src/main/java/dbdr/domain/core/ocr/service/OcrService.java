package dbdr.domain.core.ocr.service;

import dbdr.domain.core.ocr.entity.OcrData;
import dbdr.domain.core.ocr.repository.OcrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrService {
	private final OcrRepository ocrRepository;

	// OCR 데이터 저장 메서드
	@Transactional
	public OcrData saveOcrData(URL presignedUrl, String objectKey) {
		OcrData ocrData = new OcrData();
		ocrData.setUrl(presignedUrl.toString());
		ocrData.setObjectKey(objectKey);
		OcrData savedOcrData = ocrRepository.save(ocrData);

		log.info("OCR 데이터가 저장되었습니다: {}", savedOcrData);
		return savedOcrData;
	}
}
