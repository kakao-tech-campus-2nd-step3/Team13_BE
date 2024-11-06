package dbdr.domain.core.messaging.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.core.messaging.MessageTemplate;
import dbdr.domain.core.messaging.Role;
import dbdr.domain.core.messaging.dto.SqsMessageDto;
import dbdr.domain.core.messaging.entity.Alarm;
import dbdr.domain.core.messaging.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
	private final AlarmRepository alarmRepository;
	private final CallSqsService callSqsService;

	@Transactional
	public void createCareworkerAlarm(Careworker careworker) {
		Alarm alarm = Alarm.builder()
			.alertTime(LocalDateTime.now().with(LocalTime.of(17, 0))) // 오늘 17:00으로 설정
			.message(MessageTemplate.CAREWORKER_ALARM_MESSAGE.getTemplate())
			.phone(careworker.getPhone())
			.role(Role.CAREWORKER)
			.roleId(careworker.getId())
			.build();

		alarmRepository.save(alarm);
	}

	@Transactional
	public void createCareworkerNextWorkingdayAlarm(Careworker careworker) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DayOfWeek currentDay = currentDateTime.getDayOfWeek();

		// 다음 근무일 계산
		DayOfWeek nextWorkDay = careworker.getNextWorkingDay(currentDay);

		if (nextWorkDay != null) {
			// 다음 근무일의 알람 시간 설정 (해당 날짜의 alertTime 사용)
			LocalDateTime nextAlertTime = LocalDateTime.of(
				currentDateTime.with(TemporalAdjusters.next(nextWorkDay)).toLocalDate(),
				careworker.getAlertTime()
			);

			// 다음 근무일 알람 생성 및 저장
			Alarm alarm = Alarm.builder()
				.alertTime(nextAlertTime)
				.message(String.format(MessageTemplate.CAREWORKER_ALARM_MESSAGE.getTemplate(), careworker.getName()))
				.phone(careworker.getPhone())
				.role(Role.CAREWORKER)
				.roleId(careworker.getId())
				.build();

			alarmRepository.save(alarm);
		} else {
			log.warn("{} 요양보호사의 다음 근무일이 지정되지 않았습니다.", careworker.getName());
		}
	}

	@Transactional(readOnly = true)
	public Alarm getAlarmByPhoneAndAlertTime(String phone, LocalDateTime localDateTime) {
		return alarmRepository.findByPhoneAndAlertTime(phone, localDateTime).orElse(null);
	}


	@Transactional
	public void sendAlarmToSqs(Alarm alarm, String lineUserId, String name) {
		String alarmMessage = String.format(alarm.getMessage(), name);
		callSqsService.sendMessage(new SqsMessageDto(lineUserId, alarmMessage));
		alarm.setSend(true); // 메시지 전송 상태 업데이트
		alarmRepository.save(alarm);
	}

	@Transactional
	public void updateNewLineUser(String phone, String lineUserId) {
		Alarm alarm = alarmRepository.findByPhone(phone).orElse(null);
		if (alarm != null) {
			alarm.setChannel(MessageChannel.LINE);
			alarm.setChannelId(lineUserId);
			alarmRepository.save(alarm);
		}
	}
}
