package dbdr.chart.controller;

import static dbdr.util.Utils.DEFAULT_PAGE_SIZE;

import dbdr.chart.dto.response.ChartDetailResponse;
import dbdr.chart.service.ChartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/charts")
public class ChartController {
    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/recipients")
    public ResponseEntity<Page<ChartDetailResponse>> getRecipients(
            @RequestParam(value = "recipient-id", required = false) Long recipientId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ChartDetailResponse> recipients = chartService.getRecipients(recipientId, pageable);
        return ResponseEntity.ok().body(recipients);
    }

}
