package dbdr.domain.core.messaging.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.core.messaging.MessageTemplate;
import dbdr.domain.core.messaging.Role;
import dbdr.domain.core.messaging.dto.SqsMessageDto;
import dbdr.domain.core.messaging.entity.Alarm;
import dbdr.domain.core.messaging.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;

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

	@Transactional(readOnly = true)
	public Alarm getAlarmByPhone(String phone) {
		return alarmRepository.findByPhone(phone).orElse(null);
	}


	@Transactional
	public void sendAlarmToSqs(Alarm alarm, String lineUserId) {
		String message = alarm.getMessage();
		callSqsService.sendMessage(new SqsMessageDto(lineUserId, message));
		alarm.setSend(true); // 메시지 전송 상태 업데이트
		alarmRepository.save(alarm);
	}
}
