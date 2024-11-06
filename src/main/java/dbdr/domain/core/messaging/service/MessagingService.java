package dbdr.domain.core.messaging.service;

public interface MessagingService {
	void sendMessageToUser(String userId, String message);
}
