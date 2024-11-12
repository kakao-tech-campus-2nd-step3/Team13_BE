package dbdr.domain.chart.dto.response;

import java.time.LocalDate;

public record ChartOverviewResponse(
        Long chartId,
        String recipientName, // 수급자 이름
        LocalDate chartDate // 차트 작성 날짜
) {
}
