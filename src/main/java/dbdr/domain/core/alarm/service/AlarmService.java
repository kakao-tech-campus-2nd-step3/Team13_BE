package dbdr.domain.core.alarm.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.chart.dto.response.ChartDetailResponse;
import dbdr.domain.core.alarm.entity.Alarm;
import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.core.messaging.MessageTemplate;
import dbdr.domain.core.messaging.Role;
import dbdr.domain.core.messaging.dto.SqsMessageDto;
import dbdr.domain.core.alarm.repository.AlarmRepository;
import dbdr.domain.core.messaging.service.CallSqsService;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.recipient.dto.response.RecipientResponse;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.domain.recipient.service.RecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
	private final AlarmRepository alarmRepository;
	private final CallSqsService callSqsService;
	private final RecipientRepository recipientRepository;

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

	@Transactional
	public void createGuardianAlarm(Guardian guardian) {
		Alarm alarm = new Alarm(
			LocalDateTime.now().with(LocalTime.of(9, 0)), // 오늘 09:00으로 설정
			MessageTemplate.NO_CHART_MESSAGE.getTemplate(),
			guardian.getPhone(),
			Role.GUARDIAN,
			guardian.getId()
		);

		alarmRepository.save(alarm);
	}

	// 내일 알림 생성
	@Transactional
	public void createGuardianNextDayAlarm(Guardian guardian) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DayOfWeek currentDay = currentDateTime.getDayOfWeek();
		Alarm currentAlarm = getAlarmByPhone(guardian.getPhone());
		DayOfWeek nextDay = currentDay.plus(1); // 다음 날 계산
		LocalDateTime nextAlertTime = LocalDateTime.of(
			currentDateTime.with(TemporalAdjusters.next(nextDay)).toLocalDate(),
			guardian.getAlertTime()
		);
		Alarm alarm = new Alarm(
			nextAlertTime,
			currentAlarm.getChannel(),
			currentAlarm.getChannelId(),
			MessageTemplate.NO_CHART_MESSAGE.getTemplate(),
			guardian.getPhone(),
			Role.GUARDIAN,
			guardian.getId()
		);
		alarmRepository.save(alarm);
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

	@Transactional
	public void updateAlarmByLocalTime(LocalTime localTime, String phone) {
		Alarm alarm = alarmRepository.findByPhone(phone).orElse(null);
		if (alarm != null && !alarm.isSend()) {
			alarm.setAlertTime(localTime.atDate(alarm.getAlertTime().toLocalDate()));
			alarmRepository.save(alarm);
		}
	}

	@Transactional
	public void updateGuardianAlarmMessage(ChartDetailResponse chartDetailResponse) {
		Recipient recipient = recipientRepository.findById(chartDetailResponse.recipientId()).orElse(null);
		Alarm alarm = alarmRepository.findByPhone(recipient.getGuardian().getPhone()).orElse(null);

		if (alarm != null && !alarm.isSend() && alarm.getAlertTime().isAfter(LocalDateTime.now())) {
			// ChartDetailResponse의 데이터를 사용해 알림 메시지 생성
			String message = MessageTemplate.CHART_UPDATED_MESSAGE.format(
				recipient.getGuardian().getName(),
				chartDetailResponse.conditionDisease(),
				chartDetailResponse.bodyManagement().wash() ? "예" : "아니오",
				chartDetailResponse.bodyManagement().bath() ? "예" : "아니오",
				chartDetailResponse.bodyManagement().mealType(),
				chartDetailResponse.nursingManagement().systolic(),
				chartDetailResponse.nursingManagement().diastolic(),
				chartDetailResponse.nursingManagement().healthTemperature(),
				chartDetailResponse.cognitiveManagement().cognitiveHelp() ? "예" : "아니오",
				chartDetailResponse.recoveryTraining().recoveryProgram()
			);

			alarm.setMessage(message);
			alarmRepository.save(alarm);
		}
	}
}
