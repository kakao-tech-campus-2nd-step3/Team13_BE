package dbdr.domain.chart.dto.response;

public record CognitiveManagementResponse(
        Long id,
        boolean cognitiveHelp, // 의사소통 도움 여부
        boolean isCompanionshipProvided,
        String cognitiveNote // 인지 관리 특이사항
) {
}
