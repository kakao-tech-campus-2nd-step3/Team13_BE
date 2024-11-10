package dbdr.domain.chart.controller;

import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.dto.response.ChartOverviewResponse;
import dbdr.domain.chart.service.ChartService;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.model.AuthParam;
import dbdr.security.model.DbdrAuth;
import dbdr.security.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 보호자 권한 필요
@Tag(name = "[보호자] 차트 조회", description = "보호자 차트 조회")
@RestController
@RequestMapping("/${spring.app.version}/guardian/chart")
@RequiredArgsConstructor
public class GuardianChartController {
    private final ChartService chartService;

    @Operation(summary = "돌봄대상자 아이디로 차트 정보 조회")
    @GetMapping("/recipient")
    @DbdrAuth(targetRole = Role.GUARDIAN, authParam = AuthParam.RECIPIENT_ID, id = "recipientId")
    public ResponseEntity<ApiUtils.ApiResult<List<ChartOverviewResponse>>> getAllChartByRecipientId(
            @RequestParam(value = "recipient-id") Long recipientId) {
        List<ChartOverviewResponse> recipients = chartService.getAllChartByRecipientId(recipientId);
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @Operation(summary = "차트 아이디로 차트 정보 조회")
    @GetMapping("/{chartId}")
    @DbdrAuth(targetRole = Role.GUARDIAN, authParam = AuthParam.CHART_ID, id = "chartId")
    public ResponseEntity<ApiUtils.ApiResult<ChartDetailResponse>> getChartById(@PathVariable Long chartId) {
        ChartDetailResponse chart = chartService.getChartById(chartId);
        return ResponseEntity.ok(ApiUtils.success(chart));
    }
}
