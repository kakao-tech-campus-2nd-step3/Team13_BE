package dbdr.domain.chart.controller;

import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.dto.response.ChartOverviewResponse;
import dbdr.domain.chart.service.ChartService;
import dbdr.global.util.api.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 요양사 권한 필요
@Tag(name = "요양관리사 (Careworker) 차트", description = "요양관리사의 차트 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/careworker/chart")
@RequiredArgsConstructor
public class CareWorkerChartController {
    private final ChartService chartService;

    @Operation(summary = "돌봄대상자 아이디로 차트 정보 조회",
            security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/recipient")
    public ResponseEntity<ApiUtils.ApiResult<List<ChartOverviewResponse>>> getAllChartByRecipientId(
            @RequestParam(value = "recipient-id") Long recipientId) {
        // 환자 정보 접근 권한 확인 로직 필요 -> 요양사가 맡은 환자 정보만 조회 가능
        List<ChartOverviewResponse> recipients = chartService.getAllChartByRecipientId(recipientId);
        return ResponseEntity.ok(ApiUtils.success(recipients));
    }

    @Operation(summary = "차트 아이디로 차트 정보 조회",
            security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{chartId}")
    public ResponseEntity<ApiUtils.ApiResult<ChartDetailResponse>> getChartById(@PathVariable("chartId") Long chartId) {
        // 환자 정보 접근 권한 확인 로직 필요 -> 요양사가 맡은 환자 정보만 조회 가능
        ChartDetailResponse chart = chartService.getChartById(chartId);
        return ResponseEntity.ok(ApiUtils.success(chart));
    }

    @Operation(summary = "차트 추가",
            security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<ChartDetailResponse>> saveChart(@RequestBody ChartDetailRequest request) {
        // 환자 정보 접근 권한 확인 로직 필요 -> 요양사가 맡은 환자 정보만 저장 가능
        ChartDetailResponse chart = chartService.saveChart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(chart));
    }

    @Operation(summary = "차트 아이디로 차트 수정",
            security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{chartId}")
    public ResponseEntity<ApiUtils.ApiResult<ChartDetailResponse>> updateChart(@PathVariable("chartId") Long chartId,
                                                                               @RequestBody ChartDetailRequest request) {
        // 환자 정보 접근 권한 확인 로직 필요 -> 요양사가 맡은 환자 정보만 수정 가능
        ChartDetailResponse chart = chartService.updateChart(chartId, request);
        return ResponseEntity.ok(ApiUtils.success(chart));
    }

    @Operation(summary = "차트 아이디로 차트 삭제",
            security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{chartId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteChart(@PathVariable("chartId") Long chartId) {
        // 환자 정보 접근 권한 확인 로직 필요 -> 요양사가 맡은 환자 정보만 삭제 가능
        chartService.deleteChart(chartId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
