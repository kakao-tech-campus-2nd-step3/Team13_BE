package dbdr.messaging;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.core.messaging.dto.SqsMessageDto;
import dbdr.domain.core.messaging.service.CallSqsService;
import dbdr.domain.core.messaging.service.LineMessagingService;
import dbdr.domain.core.messaging.service.SmsMessagingService;

class CallSqsServiceTest {

	@Mock
	private SqsTemplate sqsTemplate;  // 제네릭 타입 없이 그대로 사용

	@Mock
	private LineMessagingService lineMessagingService;

	@Mock
	private SmsMessagingService smsMessagingService;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private CallSqsService callSqsService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSendMessage() throws Exception {
		// Mock 데이터 생성
		SqsMessageDto messageDto = new SqsMessageDto(MessageChannel.LINE, "test_user", "Test Message", "01012345678");
		String messageJson = "{ \"message\": \"Test Message\" }";
		SendResult<String> sendResult = mock(SendResult.class);

		// Mocking
		when(objectMapper.writeValueAsString(any(SqsMessageDto.class))).thenReturn(messageJson);
		when(sqsTemplate.send(any())).thenReturn((SendResult) sendResult); // 강제 형변환을 SendResult로 처리

		// 메서드 실행
		SendResult<String> result = callSqsService.sendMessage(messageDto);

		// 검증
		verify(objectMapper).writeValueAsString(messageDto);
		verify(sqsTemplate).send(any());
		assertNotNull(result);
	}

	@Test
	void testReceiveMessageWithLineChannel() throws Exception {
		// Mock 데이터 생성
		SqsMessageDto messageDto = new SqsMessageDto(MessageChannel.LINE, "test_user", "Test Message", "01012345678");
		String messageJson = "{ \"message\": \"Test Message\" }";

		// Mocking
		when(objectMapper.readValue(messageJson, SqsMessageDto.class)).thenReturn(messageDto);

		// 메서드 실행
		callSqsService.receiveMessage(messageJson);

		// 검증
		verify(lineMessagingService).pushAlarmMessage(messageDto.getUserId(), messageDto.getMessage());
		verify(smsMessagingService, never()).sendMessageToUser(anyString(), anyString());
	}

	@Test
	void testReceiveMessageWithSmsChannel() throws Exception {
		// Mock 데이터 생성
		SqsMessageDto messageDto = new SqsMessageDto(MessageChannel.SMS, null, "Test Message", "01012345678");
		String messageJson = "{ \"message\": \"Test Message\" }";

		// Mocking
		when(objectMapper.readValue(messageJson, SqsMessageDto.class)).thenReturn(messageDto);

		// 메서드 실행
		callSqsService.receiveMessage(messageJson);

		// 검증
		verify(smsMessagingService).sendMessageToUser(messageDto.getPhoneNumber(), messageDto.getMessage());
		verify(lineMessagingService, never()).pushAlarmMessage(anyString(), anyString());
	}
}
