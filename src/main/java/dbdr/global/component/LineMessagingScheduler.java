package dbdr.global.component;

import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LineMessagingScheduler {
	private final LineMessagingClient lineMessagingClient;
	private final GuardianRepository guardianRepository;
	private final CareworkerRepository careworkerRepository;

	@Scheduled(cron = "0 0/30 * * * ?")
	public void sendChartUpdate() {
		LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);  // 초와 나노초를 제거하고 분 단위로 비교

		// DB에서 알림 시간을 설정한 사용자들을 조회합니다.
		List<Guardian> guardians = guardianRepository.findByAlertTime(currentTime);
		List<Careworker> careworkers = careworkerRepository.findByAlertTime(currentTime);

		// 각 사용자에게 차트 내용을 보냅니다.
		for (Guardian guardian : guardians) {
			String userId = guardian.getLineUserId();
			String chartMessage = "오늘의 차트 내용: ...";  // 차트 정보를 DB에서 가져와서 메시지 생성

			PushMessage pushMessage = new PushMessage(userId, new TextMessage(chartMessage));

			try {
				lineMessagingClient.pushMessage(pushMessage).get();
				log.info("Message sent to user: {}", userId);
			} catch (Exception e) {
				log.error("Failed to send message to user: {}", userId, e);
			}
		}

		for (Careworker careworker : careworkers) {
			String userId = careworker.getLineUserId();
			String chartMessage = "오늘의 차트 내용: ...";  // 차트 정보를 DB에서 가져와서 메시지 생성

			PushMessage pushMessage = new PushMessage(userId, new TextMessage(chartMessage));

			try {
				lineMessagingClient.pushMessage(pushMessage).get();
				log.info("Message sent to user: {}", userId);
			} catch (Exception e) {
				log.error("Failed to send message to user: {}", userId, e);
			}
		}
	}
}
