package dbdr.domain.core.messaging.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Service
public class CallSqsService {

	private final SqsTemplate queueMessagingTemplate;

	@Value("${cloud.aws.sqs.queue-name}")
	private String QUEUE_NAME;

	public CallSqsService(SqsAsyncClient sqsAsyncClient) {
		this.queueMessagingTemplate = SqsTemplate.newTemplate(sqsAsyncClient);
	}

	public SendResult<String> sendMessage(String message) {
		System.out.println("Sender: " + message);
		return queueMessagingTemplate.send(to -> to
			.queue(QUEUE_NAME)
			.payload(message));
	}

	// 메시지를 수신하고 처리하는 메서드
	@SqsListener("${cloud.aws.sqs.queue-name}")
	public void receiveMessage(String message) {
		log.info("Received message from SQS: {}", message);

		// 여기에 보호자에게 메시지를 보내는 로직을 추가합니다.
		// 예시로 알림 서비스 호출:
		// notificationService.sendNotificationToGuardian(message);
	}
}
