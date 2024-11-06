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
import dbdr.domain.core.messaging.entity.Alarm;
import dbdr.domain.core.messaging.service.AlarmService;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LineMessagingScheduler {
	private final GuardianService guardianService;
	private final CareworkerService	careworkerService;
	private final AlarmService alarmService;

	@Scheduled(cron = "0 0/1 * * * ?")
	public void sendChartUpdate() {
		// 초와 나노초를 제거하고 분 단위로 비교하기 위해 현재 시간을 가져옴
		LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
		LocalDateTime currentDateTime = LocalDateTime.now().withSecond(0).withNano(0);

		// DB에서 알림 시간을 설정한 사용자들을 조회합니다.
		List<Guardian> guardians = guardianService.findByAlertTime(currentTime);
		List<Careworker> careworkers = careworkerService.findByAlertTime(currentTime);

		// 보호자에게 알람 메시지를 SQS로 전송합니다.
		for (Guardian guardian : guardians) {
			log.info("알림 보낼 보호자 : {}", guardian.getName());
			String phone = guardian.getPhone();
			LocalDateTime alertTime = LocalDateTime.of(LocalDate.now(), currentTime);
			Alarm alarm = alarmService.getAlarmByPhoneAndAlertTime(phone, alertTime);
			String name = guardian.getName();
			if (alarm != null && alarm.getChannel().equals(MessageChannel.LINE)) {
				alarmService.sendAlarmToSqs(alarm, alarm.getChannelId(), name);
			}
		}

		// 요양보호사에게 알람 메시지를 SQS로 전송합니다.
		for (Careworker careworker : careworkers) {
			String phone = careworker.getPhone();
			log.info("알람 시간 : {}", currentDateTime);
			Alarm alarm = alarmService.getAlarmByPhoneAndAlertTime(phone, currentDateTime);
			log.info("알림 : {}", alarm);
			String name = careworker.getName();
			if (alarm != null && alarm.getChannel().equals(MessageChannel.LINE) && !alarm.isSend()) {
				String lineUserId = alarm.getChannelId();
				log.info("알림 보낼 요양보호사 : {}", name);
				alarmService.sendAlarmToSqs(alarm, lineUserId, name);
				alarmService.createCareworkerNextWorkingdayAlarm(careworker);
			}
		}
	}
}
