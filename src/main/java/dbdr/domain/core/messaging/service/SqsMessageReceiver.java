package dbdr.domain.core.messaging.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SqsMessageReceiver {

	// 메시지를 수신하고 처리하는 메서드
	@SqsListener("${cloud.aws.sqs.queue-name}")
	public void receiveMessage(String message) {
		log.info("Received message from SQS: {}", message);

		// 여기에 보호자에게 메시지를 보내는 로직을 추가합니다.
		// 예시로 알림 서비스 호출:
		// notificationService.sendNotificationToGuardian(message);
	}
}
