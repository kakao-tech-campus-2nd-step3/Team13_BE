package dbdr.domain.core.messaging.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {
	private final SmsMessagingService smsMessagingService;

	public void sendSms(String to, String message) {
		log.info("Sending SMS to {} with message: {}", to, message);
		smsMessagingService.sendMessageToUser(to, message);
	}
}
