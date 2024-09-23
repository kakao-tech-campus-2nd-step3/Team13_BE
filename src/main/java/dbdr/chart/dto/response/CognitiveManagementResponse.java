package dbdr.chart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CognitiveManagementResponse {
    private Long id;

    private boolean cognitiveHelp; // 의사소통 도움 여부

    private String cognitiveNote; // 인지 관리 특이사항

}
