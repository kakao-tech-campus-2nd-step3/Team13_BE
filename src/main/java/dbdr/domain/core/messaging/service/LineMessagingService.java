package dbdr.domain.core.messaging.service;

import org.springframework.stereotype.Service;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineMessagingService implements MessagingService{
	private final LineMessagingClient lineMessagingClient;

	// 사용자에게 메시지를 보내는 메서드
	@Override
	public void sendMessageToUser(String userId, String message) {
		TextMessage textMessage = new TextMessage(message);
		PushMessage pushMessage = new PushMessage(userId, textMessage);

		try {
			lineMessagingClient.pushMessage(pushMessage).get();
			log.info("Message sent successfully to user: {}", userId);
		} catch (Exception e) {
			log.error("Failed to send message to user: {}", userId, e);
			throw new ApplicationException(ApplicationError.MESSAGE_SEND_FAILED);
		}
	}

	// 사용자에게 알람 메시지를 보내는 메서드
	public void pushAlarmMessage(String userId, String message) {
		TextMessage textMessage = new TextMessage(message);
		PushMessage pushMessage = new PushMessage(userId, textMessage);

		try {
			lineMessagingClient.pushMessage(pushMessage).get();
			log.info("Message sent successfully to user: {}", lineMessagingClient.getProfile(userId).get().getDisplayName());
		} catch (Exception e) {
			log.error("Failed to send message to user: {}", userId, e);
			throw new ApplicationException(ApplicationError.MESSAGE_SEND_FAILED);
		}
	}
}
