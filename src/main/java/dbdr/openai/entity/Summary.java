package dbdr.openai.entity;

import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.openai.dto.response.SummaryResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Table(name = "summaries")
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE guardians SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Summary extends BaseEntity {

    @Column(unique = true, nullable = false)
    Long chartId;
    String cognitiveManagement;
    String bodyManagement;
    String recoveryTraining;
    String conditionDisease;
    String nursingManagement;

    public void update(SummaryResponse summaryResponse){
        this.cognitiveManagement = summaryResponse.cognitiveManagement();
        this.bodyManagement = summaryResponse.bodyManagement();
        this.recoveryTraining = summaryResponse.recoveryTraining();
        this.conditionDisease = summaryResponse.conditionDisease();
        this.nursingManagement = summaryResponse.nursingManagement();
    }
}
