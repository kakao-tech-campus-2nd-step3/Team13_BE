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

    public Page<ChartDetailResponse> getRecipients(Long recipientId, Pageable pageable) {
        Page<Chart> results = chartRepository.findAllByRecipientId(recipientId, pageable);
        return results.map(ChartDetailResponse::fromEntity);
    }
}
