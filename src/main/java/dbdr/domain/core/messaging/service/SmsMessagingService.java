package dbdr.domain.core.messaging.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.model.MessageType;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsMessagingService {
	private final DefaultMessageService defaultMessageService;

	@Value("${sms.from-number}")
	private String fromNumber;

	public SmsMessagingService(@Value("${sms.api-key}") String apiKey,
		@Value("${sms.api-secret}") String apiSecret,
		@Value("${sms.domain}") String domain) {
		this.defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, domain);
	}

	public void sendMessageToUser(String userNumber, String message) {
		String subject = "안녕하세요! 돌봄다리 서비스입니다.";
		log.info("Sending LMS to user : {}", userNumber);
		log.info("Sending LMS subject : {}", subject);
		log.info("Sending LMS message : {}", message);

		Message lmsMessage = new Message();
		lmsMessage.setFrom(fromNumber);
		lmsMessage.setTo(userNumber);
		lmsMessage.setText(message);
		lmsMessage.setSubject(subject); // 제목 설정
		lmsMessage.setType(MessageType.LMS); // LMS 타입 설정

		SingleMessageSentResponse response = defaultMessageService.sendOne(new SingleMessageSendingRequest(lmsMessage));
		String statusCode = response.getStatusCode();
		if (!statusCode.equals("2000")) {
			throw new RuntimeException("Failed to send message");
		}
	}
}
