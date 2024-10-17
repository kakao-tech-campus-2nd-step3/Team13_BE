package dbdr.openai.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import dbdr.openai.dto.etc.Choice;
import dbdr.openai.dto.etc.Usage;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record OpenAiSummaryResponse(
    String id,
    String object,
    long created,
    String model,
    String systemFingerprint,
    List<Choice> choices,
    Usage usage
) {}
