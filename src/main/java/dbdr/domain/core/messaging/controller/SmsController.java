package dbdr.domain.core.messaging.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dbdr.domain.core.messaging.service.SmsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {
	private final SmsService smsService;

	@GetMapping("/send-sms/{to}/{message}")
	public void sendSms(
		@PathVariable("to") String to,
		@PathVariable("message") String message
	) {
		smsService.sendSms(to, message);
	}
}
