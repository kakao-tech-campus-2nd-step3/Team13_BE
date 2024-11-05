package dbdr.domain.core.messaging.service;

public interface MessagingService {
	abstract void sendMessageToUser(String userId, String message);
}
