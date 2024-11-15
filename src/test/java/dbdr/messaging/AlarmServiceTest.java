package dbdr.messaging;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dbdr.domain.core.alarm.entity.Alarm;
import dbdr.domain.core.alarm.repository.AlarmRepository;
import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.core.messaging.dto.SqsMessageDto;
import dbdr.domain.core.messaging.service.CallSqsService;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.openai.dto.response.SummaryResponse;
import dbdr.openai.service.SummaryService;

class AlarmServiceTest {

	@Mock
	private AlarmRepository alarmRepository;

	@Mock
	private CallSqsService callSqsService;

	@Mock
	private GuardianRepository guardianRepository;

	@Mock
	private RecipientService recipientService;

	@Mock
	private ChartRepository chartRepository;

	@Mock
	private SummaryService summaryService;

	@InjectMocks
	private AlarmService alarmService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSendAlarmToSqs() {
		Alarm alarm = mock(Alarm.class);
		String name = "테스트 보호자";
		String phoneNumber = "01012345678";
		String lineUserId = "test_line_user";

		String expectedMessage = String.format("알림 메시지 %s", name);
		SqsMessageDto messageDto = new SqsMessageDto(MessageChannel.LINE, lineUserId, expectedMessage, phoneNumber);

		when(alarm.getMessage()).thenReturn("알림 메시지 %s");
		when(alarmRepository.save(alarm)).thenReturn(alarm);

		alarmService.sendAlarmToSqs(alarm, MessageChannel.LINE, name, phoneNumber, lineUserId);

		verify(callSqsService).sendMessage(messageDto);
		verify(alarmRepository).save(alarm);
	}

	@Test
	void testGetGuardianAlarmMessage() {
		when(recipientService.isChartWrittenYesterday(anyLong())).thenReturn(true);

		Guardian guardian = mock(Guardian.class);
		when(guardian.getPhone()).thenReturn("01012345678");
		when(guardianRepository.findById(anyLong())).thenReturn(Optional.of(guardian));

		// Recipient 객체 모킹
		Recipient recipient = mock(Recipient.class);
		when(recipient.getGuardian()).thenReturn(guardian);

		// Chart 객체 모킹
		Chart chart = mock(Chart.class);
		when(chart.getRecipient()).thenReturn(recipient);
		when(chartRepository.findById(anyLong())).thenReturn(Optional.of(chart));

		// SummaryResponse 모킹
		SummaryResponse mockSummaryResponse = new SummaryResponse("condition", "body", "nursing", "cognitive", "recovery");
		when(summaryService.getSummarization(anyLong())).thenReturn(mockSummaryResponse);

		// 테스트 실행
		Alarm alarm = alarmService.getGuardianAlarmMessage(1L, LocalDateTime.now());
		assertNotNull(alarm);
		verify(recipientService).isChartWrittenYesterday(anyLong());
	}
}
