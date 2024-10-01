package dbdr.global.util;

import java.time.LocalTime;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LineMessagingUtil {

	private final LineMessagingClient lineMessagingClient;

	// 사용자에게 메시지를 보내는 메서드
	public void sendMessageToUser(String userId, String message) {
		TextMessage textMessage = new TextMessage(message);
		PushMessage pushMessage = new PushMessage(userId, textMessage);

		try {
			lineMessagingClient.pushMessage(pushMessage).get();
			log.info("Message sent successfully to user: {}", userId);
		} catch (Exception e) {
			log.error("Failed to send message to user: {}", userId, e);
			throw new RuntimeException("Failed to send message.", e);
		}
	}

	// UserId를 통해 라인 사용자 프로필 정보 가져오는 메서드
	public UserProfileResponse getUserProfile(String userId) {
		try {
			return lineMessagingClient.getProfile(userId).get();
		} catch (Exception e) {
			log.error("Failed to get user profile: {}", userId, e);
			throw new RuntimeException("Failed to get user profile.", e);
		}
	}

	// AM/PM 및 시간을 LocalTime으로 변환하는 메서드
	public LocalTime convertToLocalTime(String ampm, int hour, int minute) {
		if (ampm.equalsIgnoreCase("오후") && hour != 12) {
			hour += 12;
		} else if (ampm.equalsIgnoreCase("오전") && hour == 12) {
			hour = 0;  // 오전 12시는 0시로 변환
		}
		return LocalTime.of(hour, minute);  // 시간과 분을 함께 설정
	}
}
