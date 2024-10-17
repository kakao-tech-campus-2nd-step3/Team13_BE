package dbdr.domain.core.messaging.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dbdr.domain.core.messaging.service.LineMessagingService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/line")
@RequiredArgsConstructor
public class LineMessagingController {
	private final LineMessagingService lineMessagingService;

	@PostMapping
	public void handleLineEvent(@RequestBody String requestBody) {
		lineMessagingService.handleLineEvent(requestBody);
	}
}
