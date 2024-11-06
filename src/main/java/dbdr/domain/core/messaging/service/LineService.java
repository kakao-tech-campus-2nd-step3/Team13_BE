package dbdr.domain.core.messaging.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.profile.UserProfileResponse;

import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.core.messaging.MessageTemplate;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineService {
	private final LineMessagingClient lineMessagingClient;
	private final ObjectMapper objectMapper;
	private final GuardianService guardianService;
	private final CareworkerService careworkerService;
	private final LineMessagingService lineMessagingService;
	private final AlarmService alarmService;

	// 0. Line Event 처리
	@Transactional
	public void handleLineEvent(String requestBody) {
		try {
			JsonNode rootNode = objectMapper.readTree(requestBody);
			JsonNode eventsNode = rootNode.get("events");
			if (eventsNode != null && eventsNode.isArray()) {
				for (JsonNode eventNode : eventsNode) {
					String eventType = eventNode.get("type").asText();
					switch (eventType) {
						case "follow": // 사용자가 라인 채널을 추가하였을 때 발생하는 이벤트
							FollowEvent followEvent = objectMapper.treeToValue(eventNode, FollowEvent.class);
							handleFollowEvent(followEvent);
							break;
						case "message": // 사용자가 라인 채널에 메시지를 보냈을 때 발생하는 이벤트
							MessageEvent<TextMessageContent> messageEvent = objectMapper.treeToValue(eventNode, MessageEvent.class);
							handleMessageEvent(messageEvent);
							break;
						default:
							throw new ApplicationException(ApplicationError.CANNOT_FIND_EVENT);
					}
				}
			} else {
				throw new ApplicationException(ApplicationError.EVENT_ARRAY_NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Error processing Line event : {}", e.getMessage());
			throw new ApplicationException(ApplicationError.EVENT_ERROR);
		}
	}

	// 1. Follow Event 처리
	// 사용자가 라인 채널을 추가하였을 때 웰컴 메시지 전송
	@Transactional
	public void handleFollowEvent(FollowEvent event) {
		String userId = event.getSource().getUserId();
		lineMessagingService.sendMessageToUser(userId, MessageTemplate.FOLLOW_MESSAGE.getTemplate());
	}

	// 2. Message Event 처리
	// 사용자가 라인 채널에 메시지를 보냈을 때 발생하는 이벤트 처리
	@Transactional
	public void handleMessageEvent(MessageEvent<TextMessageContent> event) {
		String userId = event.getSource().getUserId();
		String messageText = event.getMessage().getText();
		Pattern phoneNumber = Pattern.compile("01[0-9]{8,9}"); // 전화번호 정규식
		Matcher matcherPhone = phoneNumber.matcher(messageText);

		if (matcherPhone.find()) {
			receivePhoneNumber(userId, matcherPhone.group());
		} else {
			lineMessagingService.sendMessageToUser(userId, MessageTemplate.INVALID_PHONE_INPUT_MESSAGE.getTemplate());
		}
	}

	// 사용자가 전화 번호를 입력했을 때 발생하는 이벤트 처리
	private void receivePhoneNumber(String userId, String phoneNumber) {
		String userName = getProfile(userId).getDisplayName();

		if (guardianService.findByPhone(phoneNumber) != null) {
			guardianService.updateLineUserId(userId, phoneNumber);
			alarmService.updateNewLineUser(phoneNumber, userId);
			lineMessagingService.sendMessageToUser(userId, MessageTemplate.GUARDIAN_WELCOME_MESSAGE.format(userName));
		} else if (careworkerService.findByPhone(phoneNumber) != null) {
			careworkerService.updateLineUserId(userId, phoneNumber);
			alarmService.updateNewLineUser(phoneNumber, userId);
			lineMessagingService.sendMessageToUser(userId, MessageTemplate.CAREWORKER_WELCOME_MESSAGE.format(userName));
		} else {
			// 보호자나 요양보호사가 아닌 경우
			lineMessagingService.sendMessageToUser(userId, MessageTemplate.STRANGER_FOLLOW_MESSAGE.format(userName));
		}
	}

	// UserId를 통해 라인 사용자 프로필 정보 가져오는 메서드
	public UserProfileResponse getProfile(String userId) {
		try {
			return lineMessagingClient.getProfile(userId).get();
		} catch (Exception e) {
			log.error("Failed to get user profile: {}", userId, e);
			throw new ApplicationException(ApplicationError.FAILED_TO_GET_USER_PROFILE);
		}
	}
}
