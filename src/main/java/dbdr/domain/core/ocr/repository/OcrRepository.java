package dbdr.domain.core.ocr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dbdr.domain.core.ocr.entity.OcrData;

public interface OcrRepository extends JpaRepository<OcrData, Long> {
	OcrData findByObjectKey(String objectKey);
}
