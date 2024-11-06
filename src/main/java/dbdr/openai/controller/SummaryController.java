package dbdr.openai.controller;

import dbdr.openai.dto.response.SummaryResponse;
import dbdr.openai.service.SummarizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "요약 API", description = "차트 하루 요약")
@RestController
@RequiredArgsConstructor
@RequestMapping("/${spring.app.version}/summary")
public class SummaryController {

    private final SummarizationService summarizationService;

    @Operation(summary = "해당 날짜와 돌봄대상자 id로 요약을 해준다.", description = "시작 날짜만 필수이고, 끝 날짜는 필수가 아니다. 끝나는 날짜를 넣지 않으면 자동으로 하루치만 요약한다.")
    @PostMapping
    public ResponseEntity<SummaryResponse> getSummary(@RequestParam("recipientId") Long recipientId,
        @RequestParam("startDate")
        LocalDateTime startDate,
        @RequestParam(name = "endDate", required = false) LocalDateTime endDate) {
        if(endDate == null){
            endDate = startDate;
        }
        return ResponseEntity.ok(
            summarizationService.getTextAndGetSummary(recipientId, startDate, endDate));
    }
}
