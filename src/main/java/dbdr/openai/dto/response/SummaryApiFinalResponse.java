package dbdr.openai.dto.response;

import java.time.LocalDate;

public record SummaryApiFinalResponse(SummaryResponse summaryResponse, TagResponse tagResponse,
                                      LocalDate updatedAt, String institutionName) {

}
