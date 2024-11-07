package dbdr.domain.core.messaging.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;


@Service
public class SmsMessagingService implements MessagingService {
	private final DefaultMessageService defaultMessageService;

	@Value("${sms.from-number}")
	private String fromNumber;

	public SmsMessagingService(@Value("${sms.api-key}") String apiKey,
		@Value("${sms.api-secret}") String apiSecret,
		@Value("${sms.domain}") String domain) {
		this.defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, domain);
	}

	@Override
	public void sendMessageToUser(String userNumber, String message) {
		Message smsMessage = new Message();
		smsMessage.setFrom(fromNumber);
		smsMessage.setTo(userNumber);
		smsMessage.setText(message);

		SingleMessageSentResponse response = defaultMessageService.sendOne(new SingleMessageSendingRequest(smsMessage));
		String statusCode = response.getStatusCode();
		if (!statusCode.equals("2000")) {
			throw new RuntimeException("Failed to send message");
		}
	}
}
