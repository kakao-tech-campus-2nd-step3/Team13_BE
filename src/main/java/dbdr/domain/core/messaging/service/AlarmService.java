package dbdr.domain.core.messaging.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jose.crypto.impl.AAD;

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
		Alarm alarm = new Alarm(
			LocalDateTime.now().with(LocalTime.of(17, 0)), // 오늘 17:00으로 설정
			MessageTemplate.CAREWORKER_ALARM_MESSAGE.getTemplate(),
			careworker.getPhone(),
			Role.CAREWORKER,
			careworker.getId()
		);

		alarmRepository.save(alarm);
	}

	@Transactional
	public void createCareworkerNextWorkingdayAlarm(Careworker careworker) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DayOfWeek currentDay = currentDateTime.getDayOfWeek();
		Alarm currentAlarm = getAlarmByPhone(careworker.getPhone());
		DayOfWeek nextWorkDay = careworker.getNextWorkingDay(currentDay); // 다음 근무일 계산
		if (nextWorkDay != null) {
			LocalDateTime nextAlertTime = LocalDateTime.of(
				currentDateTime.with(TemporalAdjusters.next(nextWorkDay)).toLocalDate(),
				careworker.getAlertTime()
			);
			Alarm alarm = new Alarm(
				nextAlertTime,
				currentAlarm.getChannel(),
				currentAlarm.getChannelId(),
				MessageTemplate.CAREWORKER_ALARM_MESSAGE.getTemplate(),
				careworker.getPhone(),
				Role.CAREWORKER,
				careworker.getId()
			);
			alarmRepository.save(alarm);
		} else {
			log.warn("{} 요양보호사의 다음 근무일이 지정되지 않았습니다.", careworker.getName());
		}
	}

	@Transactional(readOnly = true)
	public Alarm getAlarmByPhoneAndAlertTime(String phone, LocalDateTime alertTime) {
		return alarmRepository.findByPhoneAndAlertTime(phone, alertTime).orElse(null);
	}

	@Transactional(readOnly = true)
	public Alarm getAlarmByPhone(String phone) {
		return alarmRepository.findByPhone(phone).orElse(null);
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
