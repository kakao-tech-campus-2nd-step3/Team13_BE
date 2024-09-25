package dbdr.chart.controller;

import static dbdr.util.Utils.DEFAULT_PAGE_SIZE;

import dbdr.chart.dto.response.ChartDetailResponse;
import dbdr.chart.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 로그인 사용자 권한 필요
@RestController
@RequestMapping("/v1/charts")
@RequiredArgsConstructor
public class CareWorkerChartController {
    private final ChartService chartService;

    @GetMapping("/recipients")
    public ResponseEntity<Page<ChartDetailResponse>> getAllChartByRecipientId(
            @RequestParam(value = "recipient-id", required = false) Long recipientId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // 환자 정보 접근 권한 확인 로직 필요
        Page<ChartDetailResponse> recipients = chartService.getAllChartByRecipientId(recipientId, pageable);
        return ResponseEntity.ok().body(recipients);
    }

    @GetMapping("/{chartId}")
    public ResponseEntity<ChartDetailResponse> getChartById(@PathVariable Long chartId) {
        // 환자 정보 접근 권한 확인 로직 필요
        ChartDetailResponse chart = chartService.getChartById(chartId);
        return ResponseEntity.ok().body(chart);
    }

    @DeleteMapping("/{chartId}")
    public ResponseEntity<?> deleteChart(@PathVariable Long chartId) {
        // 환자 정보 접근 권한 확인 로직 필요
        chartService.deleteChart(chartId);
        return ResponseEntity.ok().build();
    }

}
