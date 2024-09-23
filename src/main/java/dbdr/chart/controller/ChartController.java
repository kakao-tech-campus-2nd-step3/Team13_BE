package dbdr.chart.controller;

import dbdr.chart.service.ChartService;
import dbdr.dto.response.RecipientResponseDTO;
import java.util.List;
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
    public ResponseEntity<List<RecipientResponseDTO>> getRecipients(@RequestParam(value = "recipient-id", required = false) Long recipientId{
        List<RecipientResponseDTO> recipients = chartService.getRecipients();
        return ResponseEntity.ok(recipients);
    }

}
