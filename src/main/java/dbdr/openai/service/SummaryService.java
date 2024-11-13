package dbdr.openai.service;

import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.openai.dto.response.SummaryApiFinalResponse;
import dbdr.openai.dto.response.SummaryResponse;
import dbdr.openai.dto.response.TagResponse;
import dbdr.openai.entity.Summary;
import dbdr.openai.repository.SummaryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final ChartRepository chartRepository;

    public SummaryApiFinalResponse getFinalSummary(Long chartId) {
        Chart chart = chartRepository.findById(chartId).orElseThrow(() -> new ApplicationException(
            ApplicationError.CHART_NOT_FOUND));
        String institutionName = chart.getRecipient().getInstitution().getInstitutionName();
        LocalDateTime dateTime = chart.getUpdatedAt();
        LocalDate date = dateTime.toLocalDate();
        return new SummaryApiFinalResponse(getSummarization(chartId), getTag(chartId), date,
            institutionName);
    }

    public SummaryResponse getSummarization(Long chartId) {
        Summary summary = summaryRepository.findByChartId(chartId);
        return new SummaryResponse(summary.getConditionDisease(), summary.getBodyManagement(),
            summary.getNursingManagement(), summary.getCognitiveManagement(),
            summary.getRecoveryTraining());
    }

    private TagResponse getTag(Long chartId) {
        Summary summary = summaryRepository.findByChartId(chartId);
        return new TagResponse(summary.getTagOne(), summary.getTagTwo(), summary.getTagThree());
    }
}
