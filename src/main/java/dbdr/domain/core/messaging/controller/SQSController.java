package dbdr.domain.core.messaging.controller;

import dbdr.domain.core.messaging.service.SqsMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SQSController {
	private final SqsMessageSender sqsMessageSender;

	@PostMapping("/test/message")
	public void sendMessage(@RequestBody String message) {
		sqsMessageSender.sendMessage(message);
	}
}
