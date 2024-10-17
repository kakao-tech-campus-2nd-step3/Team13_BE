package dbdr.openai.dto.request;

import dbdr.openai.dto.etc.Message;
import java.util.List;

public record OpenAiSummaryRequest(String model, List<Message> messages) {
}
