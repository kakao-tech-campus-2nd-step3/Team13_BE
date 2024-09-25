package dbdr.chart.service;

import dbdr.chart.domain.Chart;
import dbdr.chart.dto.response.ChartDetailResponse;
import dbdr.chart.repository.ChartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChartService {
    private final ChartRepository chartRepository;

    public ChartService(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    public Page<ChartDetailResponse> getAllChartByRecipientId(Long recipientId, Pageable pageable) {
        Page<Chart> results = chartRepository.findAllByRecipientId(recipientId, pageable);
        return results.map(ChartDetailResponse::fromEntity);
    }

    public ChartDetailResponse getChartById(Long chartId) {
        Chart chart = chartRepository.findById(chartId).orElseThrow(); // 에러 처리 필요
        return ChartDetailResponse.fromEntity(chart);
    }

    public void deleteChart(Long chartId) {
        chartRepository.deleteById(chartId);
    }
}
