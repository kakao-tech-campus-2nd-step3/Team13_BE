package dbdr.openai.service;

import dbdr.domain.chart.service.ChartService;
import dbdr.openai.dto.response.OpenAiSummaryResponse;
import dbdr.openai.dto.response.SummaryAndTagResponse;
import dbdr.openai.dto.response.SummaryResponse;
import dbdr.openai.dto.response.TagResponse;
import dbdr.openai.entity.Summary;
import dbdr.openai.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final ChartService chartService;

    @Value("${openai.model-tag}")
    private String modelTwo;

    public SummaryAndTagResponse getSummaryAndTag(Long chartId) {
        return new SummaryAndTagResponse(getSummarization(chartId), getTag(chartId));
    }

    private SummaryResponse getSummarization(Long chartId) {
        Summary summary = summaryRepository.findByChartId(chartId);
        return new SummaryResponse(summary.getCognitiveManagement(), summary.getBodyManagement(),
            summary.getRecoveryTraining(), summary.getConditionDisease(),
            summary.getNursingManagement());
    }

    private TagResponse getTag(Long chartId) {
        Summary summary = summaryRepository.findByChartId(chartId);
        String str = String.format(
            "%s, %s, %s, %s, %s",
            summary.getCognitiveManagement(), summary.getBodyManagement(),
            summary.getRecoveryTraining(), summary.getConditionDisease(), summary.getNursingManagement()
        );
        OpenAiSummaryResponse response = chartService.openAiResponse(str, modelTwo);
        return parseTagString(response.choices().get(0).message().content());
    }

    private TagResponse parseTagString(String tagString) {
        String[] tags = tagString.split(", ");
        String tag1 = tags[0].split(": ")[1].trim();
        String tag2 = tags[1].split(": ")[1].trim();
        String tag3 = tags[2].split(": ")[1].trim();

        return new TagResponse(tag1, tag2, tag3);
    }
}
