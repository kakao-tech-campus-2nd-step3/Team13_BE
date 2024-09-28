package dbdr.domain.core.messaging.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LineMessagingScheduler {
	private final LineMessagingClient lineMessagingClient;

	@Scheduled(cron = "0 0 21 * * ?")  // 매일 오후 9시에 실행 (크론 표현식은 특정 시간에 맞게 설정 가능)
	public void sendChartUpdate() {
		// DB에서 알림 시간을 설정한 사용자들을 조회
		// 각 사용자에게 차트 내용을 보냅니다.

		String userId = "USER_ID_FROM_DB";  // 예시로 DB에서 가져온 User ID
		String chartMessage = "오늘의 차트 내용: ...";  // 차트 정보를 DB에서 가져와서 메시지 생성

		PushMessage pushMessage = new PushMessage(userId, new TextMessage(chartMessage));

		try {
			lineMessagingClient.pushMessage(pushMessage).get();
		} catch (Exception e) {
			log.error("Failed to send message to user: {}", userId);
		}
	}
}
