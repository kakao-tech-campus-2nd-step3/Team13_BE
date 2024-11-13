package dbdr.domain.core.messaging.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.core.alarm.entity.Alarm;
import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessagingScheduler {
	private final GuardianService guardianService;
	private final CareworkerService	careworkerService;
	private final AlarmService alarmService;

	@Scheduled(cron = "0 0/1 * * * ?", zone = "Asia/Seoul")
	public void sendChartUpdate() {
		// 현재 시간 출력
		log.info("현재 시간 : {}", LocalDateTime.now());
		// 초와 나노초를 제거하고 분 단위로 비교하기 위해 현재 시간을 가져옴
		LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
		LocalDateTime currentDateTime = LocalDateTime.now().withSecond(0).withNano(0);

		// 현재 시간에 알람을 받아야 하는 보호자와 요양보호사를 가져옴
		List<Guardian> guardians = guardianService.findByAlertTime(currentTime);
		List<Careworker> careworkers = careworkerService.findByAlertTime(currentTime);

		// 보호자에게 알람 메시지를 SQS로 전송합니다.
		for (Guardian guardian : guardians) {
			Alarm alarm = alarmService.getGuardianAlarmMessage(guardian.getId(), currentDateTime); // 보호자의 알람
			String name = guardian.getName();

			// (1) Line 채널 알림 보내기
			if (alarm != null && guardian.isLineSubscription()) {
				log.info("Line 알림 메세지를 받을 보호자 : {}", name);
				alarmService.sendAlarmToSqs(alarm, MessageChannel.LINE, name, guardian.getPhone(), guardian.getLineUserId());
			}
			// (2) SMS 알림 보내기
			if (alarm != null && guardian.isSmsSubscription()) {
				log.info("SMS 문자 알림 메세지를 받을 보호자 : {}", name);
				alarmService.sendAlarmToSqs(alarm, MessageChannel.SMS, name, guardian.getPhone(), guardian.getLineUserId());
			}
		}

		// 요양보호사에게 알람 메시지를 SQS로 전송합니다.
		for (Careworker careworker : careworkers) {
			Alarm alarm = alarmService.getCareworkerAlarmMessage(careworker.getId(), currentDateTime); // 요양보호사의 알람
			String name = careworker.getName();

			// (1) Line 채널 알림 보내기
			if (alarm != null && careworker.isLineSubscription()) {
				log.info("Line 알림 메세지를 받을 보호자 : {}", name);
				alarmService.sendAlarmToSqs(alarm, MessageChannel.LINE, name, careworker.getPhone(), careworker.getLineUserId());
			}
			// (2) SMS 알림 보내기
			if (alarm != null && careworker.isSmsSubscription()) {
				log.info("SMS 문자 알림 메세지를 받을 보호자 : {}", name);
				alarmService.sendAlarmToSqs(alarm, MessageChannel.SMS, name, careworker.getPhone(), careworker.getLineUserId());
			}
		}
	}
}
