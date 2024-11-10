package dbdr.domain.chart.dto.response;

public record CognitiveManagementResponse(
        Long id,
        boolean cognitiveHelp, // 의사소통 도움 여부
        boolean companionshipProvided,
        String cognitiveNote // 인지 관리 특이사항
) {
}
