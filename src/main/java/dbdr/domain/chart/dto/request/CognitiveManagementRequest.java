package dbdr.domain.chart.dto.request;

public record CognitiveManagementRequest(
        boolean cognitiveHelp, // 의사소통 도움 여부
        String cognitiveNote // 인지 관리 특이사항
) {
}
