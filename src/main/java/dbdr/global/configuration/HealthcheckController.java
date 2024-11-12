package dbdr.global.configuration;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthcheckController {
	@GetMapping(value = "/health")
	public ResponseEntity<?> healthcheck() {
		return ResponseEntity.ok()
			.body("OK");
	}
}
