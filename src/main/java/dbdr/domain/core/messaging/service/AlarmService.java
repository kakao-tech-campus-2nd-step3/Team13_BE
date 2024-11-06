package dbdr.domain.core.messaging.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.core.messaging.MessageTemplate;
import dbdr.domain.core.messaging.Role;
import dbdr.domain.core.messaging.entity.Alarm;
import dbdr.domain.core.messaging.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {

	private final AlarmRepository alarmRepository;

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
}
