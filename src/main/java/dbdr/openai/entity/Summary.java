package dbdr.openai.entity;

import dbdr.domain.core.base.entity.BaseEntity;
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
    String tagOne;
    String tagTwo;
    String tagThree;

    public void update(String cognitiveManagement, String bodyManagement, String recoveryTraining,
        String conditionDisease, String nursingManagement, String tagOne, String tagTwo, String tagThree) {
        this.cognitiveManagement =cognitiveManagement;
        this.bodyManagement = bodyManagement;
        this.recoveryTraining = recoveryTraining;
        this.conditionDisease = conditionDisease;
        this.nursingManagement = nursingManagement;
        this.tagOne = tagOne;
        this.tagTwo = tagTwo;
        this.tagThree = tagThree;
    }
}
