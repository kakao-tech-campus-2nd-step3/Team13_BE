package dbdr.domain.chart.controller;

import static dbdr.global.util.api.Utils.DEFAULT_PAGE_SIZE;

import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.dto.response.ChartOverviewResponse;
import dbdr.domain.chart.service.ChartService;
import dbdr.global.util.api.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 보호자 권한 필요
@Tag(name = "보호자 (Guardian) 차트", description = "보호자 차트 조회")
@RestController
@RequestMapping("/${spring.app.version}/guardian/chart")
@RequiredArgsConstructor
public class GuardianChartController {
    private final ChartService chartService;

    @Operation(summary = "돌봄대상자 아이디로 차트 정보 조회")
    @GetMapping("/recipient")
    public ResponseEntity<ApiUtils.ApiResult<List<ChartOverviewResponse>>> getAllChartByRecipientId(
            @RequestParam(value = "recipient-id") Long recipientId) {
        // 환자 정보 접근 권한 확인 로직 필요 -> 보호자가 자신의 환자 정보만 조회 가능
        List<ChartOverviewResponse> recipients = chartService.getAllChartByRecipientId(recipientId);
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @Operation(summary = "차트 아이디로 차트 정보 조회")
    @GetMapping("/{chartId}")
    public ResponseEntity<ApiUtils.ApiResult<ChartDetailResponse>> getChartById(@PathVariable Long chartId) {
        // 환자 정보 접근 권한 확인 로직 필요 -> 보호자가 자신의 환자 정보만 조회 가능
        ChartDetailResponse chart = chartService.getChartById(chartId);
        return ResponseEntity.ok(ApiUtils.success(chart));
    }
}
