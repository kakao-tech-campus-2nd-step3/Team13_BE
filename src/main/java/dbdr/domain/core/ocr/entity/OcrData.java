package dbdr.domain.core.ocr.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import dbdr.domain.core.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ocr_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE ocr_data SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class OcrData extends BaseEntity {
	@Column(nullable = false, unique = true)
	private String objectKey;

	@Lob
	@Column(nullable = true, columnDefinition = "TEXT") // 명시적으로 TEXT로 설정
	private String ocrResult;
}
