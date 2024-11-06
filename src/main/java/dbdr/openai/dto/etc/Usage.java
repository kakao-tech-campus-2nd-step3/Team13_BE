package dbdr.openai.dto.etc;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record Usage(
    int promptTokens,
    int completionTokens,
    int totalTokens,
    CompletionTokensDetails completionTokensDetails
) {}
