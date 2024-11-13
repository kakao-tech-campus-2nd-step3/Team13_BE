package dbdr.domain.core.messaging.dto;

import dbdr.domain.core.messaging.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqsMessageDto {
	private MessageChannel messageChannel;
	private String userId;
	private String message;
	private String phoneNumber;
}
