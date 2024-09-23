package dbdr.chart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecoveryTrainingResponse {
    private Long id;
    private String recoveryProgram; // 회복 프로그램 이름
    private boolean recoveryTraining; // 회복훈련 완료 여부
    private String recoveryNote; // 회복훈련 특이사항
}
