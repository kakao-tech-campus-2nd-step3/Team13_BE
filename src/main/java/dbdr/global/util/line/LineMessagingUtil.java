package dbdr.global.util.line;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LineMessagingUtil {
	private final LineMessagingClient lineMessagingClient;

	public void userFoundFailedMessage(String userId) {
		String errorMessage =
			"등록된 정보가 확인되지 않았습니다. 😊\n" +
				"서비스 이용을 위해 요양원에 문의하시거나, 전화번호를 다시 인증해주시기 바랍니다. ☎️\n" +
				"예: 01012345678";
		sendMessageToUser(userId, errorMessage);
	}

	// 사용자에게 메시지를 보내는 메서드
	public void sendMessageToUser(String userId, String message) {
		TextMessage textMessage = new TextMessage(message);
		PushMessage pushMessage = new PushMessage(userId, textMessage);

		try {
			lineMessagingClient.pushMessage(pushMessage).get();
			log.info("Message sent successfully to user: {}", userId);
		} catch (Exception e) {
			log.error("Failed to send message to user: {}", userId, e);
			throw new ApplicationException(ApplicationError.MESSAGE_SEND_FAILED);
		}
	}

	// UserId를 통해 라인 사용자 프로필 정보 가져오는 메서드
	public UserProfileResponse getUserProfile(String userId) {
		try {
			return lineMessagingClient.getProfile(userId).get();
		} catch (Exception e) {
			log.error("Failed to get user profile: {}", userId, e);
			throw new ApplicationException(ApplicationError.FAILED_TO_GET_USER_PROFILE);
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
