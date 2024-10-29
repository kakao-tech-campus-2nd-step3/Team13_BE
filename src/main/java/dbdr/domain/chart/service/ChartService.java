package dbdr.domain.chart.service;

import dbdr.domain.chart.dto.ChartMapper;
import dbdr.domain.chart.dto.request.ChartDetailRequest;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.repository.ChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final ChartRepository chartRepository;
    private final ChartMapper chartMapper;

    public Page<ChartDetailResponse> getAllChartByRecipientId(Long recipientId, Pageable pageable) {
        Page<Chart> results = chartRepository.findAllByRecipientId(recipientId, pageable);
        return results.map(chartMapper::toResponse);
    }

    public ChartDetailResponse getChartById(Long chartId) {
        Chart chart = chartRepository.findById(chartId).orElseThrow(); // 에러 처리 필요
        return chartMapper.toResponse(chart);
    }

    public void deleteChart(Long chartId) {
        chartRepository.deleteById(chartId);
    }

    public ChartDetailResponse saveChart(ChartDetailRequest request) {
        Chart chart = chartMapper.toEntity(request);
        Chart savedChart = chartRepository.save(chart);
        return chartMapper.toResponse(savedChart);
    }

    public ChartDetailResponse updateChart(Long chartId, ChartDetailRequest request) {
        Chart chart = chartRepository.findById(chartId).orElseThrow(); // 에러 처리 필요
        chart.update(chartMapper.toEntity(request));
        Chart savedChart = chartRepository.save(chart);
        return chartMapper.toResponse(savedChart);
    }
}
