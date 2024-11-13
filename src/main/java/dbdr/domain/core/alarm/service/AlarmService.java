package dbdr.domain.core.alarm.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.core.alarm.entity.Alarm;
import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.core.messaging.MessageTemplate;
import dbdr.domain.core.messaging.Role;
import dbdr.domain.core.messaging.dto.SqsMessageDto;
import dbdr.domain.core.alarm.repository.AlarmRepository;
import dbdr.domain.core.messaging.service.CallSqsService;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.openai.dto.response.SummaryResponse;
import dbdr.openai.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
	private final AlarmRepository alarmRepository;
	private final CallSqsService callSqsService;
	private final RecipientService recipientService;
	private final CareworkerRepository careworkerRepository;
	private final GuardianRepository guardianRepository;
	private final ChartRepository chartRepository;
	private final SummaryService summaryService;


	@Transactional
	public void sendAlarmToSqs(Alarm alarm, MessageChannel messageChannel, String name, String phoneNumber, String lineUserId) {
		String alarmMessage = String.format(alarm.getMessage(), name);
		callSqsService.sendMessage(new SqsMessageDto(messageChannel, lineUserId, alarmMessage, phoneNumber));
		log.info("알림 메시지를 SQS로 전송했습니다. 이름 : {}, 메시지 : {}, 메세지 채널 : {}", name, alarmMessage, MessageChannel.valueOf(messageChannel.name()));
		alarm.setSend(true);
		alarm.setChannel(messageChannel);
		alarmRepository.save(alarm);
	}

	// 보호자 알림 메시지 생성
	@Transactional
	public Alarm getGuardianAlarmMessage(Long guardianId, LocalDateTime currentDateTime) {
		// 어제 날짜로 차트 데이터가 있는지 확인하기
		boolean isChartWrittenYesterday = recipientService.isChartWrittenYesterday(guardianId);
		log.info("어제 날짜로 차트 데이터가 있는지 확인하기 : {}", isChartWrittenYesterday);
		if (isChartWrittenYesterday) { // 어제자 차트 내용으로 알림 메시지 생성
			Long chartId = recipientService.getChartIdByGuardianId(guardianId);
			Chart chart = chartRepository.findById(chartId).orElseThrow(() -> new IllegalArgumentException("차트가 존재하지 않습니다."));
			SummaryResponse summaryResponse = summaryService.getSummarization(chartId);
			String message = MessageTemplate.CHART_UPDATED_MESSAGE.format(
				guardianRepository.findById(guardianId).orElseThrow(() -> new IllegalArgumentException("보호자가 존재하지 않습니다.")).getName(),
				summaryResponse.bodyManagement(),
				summaryResponse.cognitiveManagement(),
				summaryResponse.nursingManagement(),
				summaryResponse.recoveryTraining()
			);

			return new Alarm(
				currentDateTime,
				message,
				chart.getRecipient().getGuardian().getPhone(),
				Role.GUARDIAN,
				guardianId
			);
		} else {
			return new Alarm(
				currentDateTime,
				MessageTemplate.NO_CHART_MESSAGE.getTemplate(),
				guardianRepository.findById(guardianId).orElseThrow(() -> new IllegalArgumentException("보호자가 존재하지 않습니다.")).getPhone(),
				Role.GUARDIAN,
				guardianId
			);
		}
	}

	// 요양보호사 알림 메시지 생성
	public Alarm getCareworkerAlarmMessage(Long careworkerId, LocalDateTime currentDateTime) {
		return new Alarm(
			currentDateTime,
			MessageTemplate.CAREWORKER_ALARM_MESSAGE.getTemplate(),
			careworkerRepository.findById(careworkerId).orElseThrow(() -> new IllegalArgumentException("요양보호사가 존재하지 않습니다.")).getPhone(),
			Role.CAREWORKER,
			careworkerId
		);
	}
}
