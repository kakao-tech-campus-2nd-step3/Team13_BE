package dbdr.messaging;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.core.alarm.entity.Alarm;
import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.domain.core.messaging.util.MessagingScheduler;

class MessagingSchedulerTest {

	@Mock
	private GuardianService guardianService;

	@Mock
	private CareworkerService careworkerService;

	@Mock
	private AlarmService alarmService;

	@InjectMocks
	private MessagingScheduler messagingScheduler;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSendChartUpdate() {
		// Mock 데이터 생성
		Guardian guardian = mock(Guardian.class);
		Careworker careworker = mock(Careworker.class);
		Alarm guardianAlarm = mock(Alarm.class);
		Alarm careworkerAlarm = mock(Alarm.class);

		when(guardianService.findByAlertTime(any(LocalTime.class))).thenReturn(List.of(guardian));
		when(careworkerService.findByAlertTime(any(LocalTime.class))).thenReturn(List.of(careworker));
		when(alarmService.getGuardianAlarmMessage(anyLong(), any(LocalDateTime.class))).thenReturn(guardianAlarm);
		when(alarmService.getCareworkerAlarmMessage(anyLong(), any(LocalDateTime.class))).thenReturn(careworkerAlarm);
		when(guardian.isLineSubscription()).thenReturn(true);
		when(guardian.isSmsSubscription()).thenReturn(true);
		when(careworker.isLineSubscription()).thenReturn(true);
		when(careworker.isSmsSubscription()).thenReturn(true);
		when(careworker.isWorkingOn(any())).thenReturn(true);

		// 메서드 실행
		messagingScheduler.sendChartUpdate();

		// 알람 서비스 호출 확인
		verify(alarmService).sendAlarmToSqs(guardianAlarm, MessageChannel.LINE, guardian.getName(), guardian.getPhone(), guardian.getLineUserId());
		verify(alarmService).sendAlarmToSqs(guardianAlarm, MessageChannel.SMS, guardian.getName(), guardian.getPhone(), guardian.getLineUserId());
		verify(alarmService).sendAlarmToSqs(careworkerAlarm, MessageChannel.LINE, careworker.getName(), careworker.getPhone(), careworker.getLineUserId());
		verify(alarmService).sendAlarmToSqs(careworkerAlarm, MessageChannel.SMS, careworker.getName(), careworker.getPhone(), careworker.getLineUserId());
	}
}
