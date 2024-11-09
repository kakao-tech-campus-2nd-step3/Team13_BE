package dbdr.domain.core.messaging.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import dbdr.domain.core.messaging.dto.SqsMessageDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallSqsService {

	private final SqsTemplate queueMessagingTemplate;
	private final LineMessagingService lineMessagingService;
	private final ObjectMapper objectMapper; // JSON 변환용 ObjectMapper

	@Value("${cloud.aws.sqs.queue-name}")
	private String QUEUE_NAME;

	// SqsMessageDto를 받아 JSON 형식으로 변환해 SQS에 전송
	public SendResult<String> sendMessage(SqsMessageDto messageDto) {
		try {
			String messageJson = objectMapper.writeValueAsString(messageDto);
			log.info("Sending message to SQS: {}", messageJson);
			return queueMessagingTemplate.send(to -> to
				.queue(QUEUE_NAME)
				.payload(messageJson));
		} catch (Exception e) {
			log.error("Failed to send message to SQS", e);
			throw new RuntimeException("Failed to send message", e);
		}
	}

	// SQS에서 메시지를 수신하고 사용자에게 전달
	@SqsListener("${cloud.aws.sqs.queue-name}")
	public void receiveMessage(String messageJson) {
		try {
			// JSON을 SqsMessageDto 객체로 변환
			SqsMessageDto messageDto = objectMapper.readValue(messageJson, SqsMessageDto.class);
			log.info("Received message from SQS: {}", messageDto);

			// LineMessagingService를 통해 사용자에게 메시지 전송
			lineMessagingService.pushAlarmMessage(messageDto.getUserId(), messageDto.getMessage());
		} catch (Exception e) {
			log.error("Failed to process message from SQS", e);
		}
	}
}
