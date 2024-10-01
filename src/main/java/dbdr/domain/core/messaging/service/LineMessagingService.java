package dbdr.domain.core.messaging.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;

import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.global.util.LineMessagingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineMessagingService {

	private final ObjectMapper objectMapper;
	private final GuardianMessagingService guardianMessagingService;
	private final CareworkerMessagingService careworkerMessagingService;
	private final LineMessagingUtil lineMessagingUtil;
	private final GuardianService guardianService;
	private final CareworkerService careworkerService;

	@Transactional
	public void handleLineEvent(String requestBody) {
		try {
			JsonNode rootNode = objectMapper.readTree(requestBody);
			JsonNode eventsNode = rootNode.get("events");

			if (eventsNode != null && eventsNode.isArray()) {
				for (JsonNode eventNode : eventsNode) {
					String eventType = eventNode.get("type").asText();

					switch (eventType) {
						case "follow":
							log.info("Follow event 발생");
							FollowEvent followEvent = objectMapper.treeToValue(eventNode, FollowEvent.class);
							handleFollowEvent(followEvent);
							break;
						case "message":
							MessageEvent<TextMessageContent> messageEvent = objectMapper.treeToValue(eventNode, MessageEvent.class);
							handleMessageEvent(messageEvent);
							break;
						case "unfollow":
							log.info("Unfollow event 발생");
							break;
						default:
							throw new ApplicationException(ApplicationError.CANNOT_FIND_EVENT);
					}
				}
			} else {
				throw new ApplicationException(ApplicationError.EVENT_ARRAY_NOT_FOUND);
			}
		} catch (Exception e) {
			throw new ApplicationException(ApplicationError.EVENT_ERROR);
		}
	}

	// 1. Follow Event 처리
	// 사용자가 라인 채널을 추가하였을 때 발생하는 이벤트 처리
	@Transactional
	public void handleFollowEvent(FollowEvent event) {
		String userId = event.getSource().getUserId();
		String userName = lineMessagingUtil.getUserProfile(userId).getDisplayName();

		if (guardianService.nameExists(userName)) {
			guardianMessagingService.handleGuardianFollowEvent(userId, userName);
		} else if (careworkerService.nameExists(userName)) {
			careworkerMessagingService.handleCareworkerFollowEvent(userId, userName);
		} else {
			sendStrangerFollowMessage(userId, userName);
		}
	}

	// 2. Message Event 처리
	// 사용자가 알림 예약 메시지를 보냈을 때 발생하는 이벤트 처리
	@Transactional
	public void handleMessageEvent(MessageEvent<TextMessageContent> event) {
		String userId = event.getSource().getUserId();
		String messageText = event.getMessage().getText();

		// '오전 9시 30분', '오후 3시'와 같은 형식을 처리하는 정규식
		Pattern pattern = Pattern.compile("(오전|오후)\\s*(\\d{1,2})시\\s*(\\d{1,2})?분?");
		Matcher matcher = pattern.matcher(messageText);

		if (matcher.find()) {
			String ampm = matcher.group(1);  // '오전' 또는 '오후'
			String hour = matcher.group(2);  // 시간
			String minute = matcher.group(3) != null ? matcher.group(3) : "0";  // 분이 없는 경우 기본값 0

			log.info("추출된 시간: {} {}, {}분", ampm, hour, minute);

			String confirmationMessage =
				"감사합니다! 😊\n" +
					"입력하신 시간 " + messageText + "에 알림을 보내드릴게요. 💬\n" +
					"언제든지 알림 시간을 변경하고 싶으시면 다시 알려주세요!";

			if (guardianService.findByLineUserId(userId) != null) {
				lineMessagingUtil.sendMessageToUser(userId, confirmationMessage);
				guardianMessagingService.saveGuardianAlertTime(userId, ampm, hour, minute);
			} else if (careworkerService.findByLineUserId(userId) != null) {
				lineMessagingUtil.sendMessageToUser(userId, confirmationMessage);
				careworkerMessagingService.saveCareworkerAlertTime(userId, ampm, hour, minute);
			} else {
				throw new ApplicationException(ApplicationError.USER_NOT_FOUND);
			}
		} else {
			String errorMessage = "알림 시간을 인식할 수 없습니다. '오전 9시 30분' 또는 '오후 3시'와 같은 형식으로 입력해주세요!";
			lineMessagingUtil.sendMessageToUser(userId, errorMessage);
		}
	}

	private void sendStrangerFollowMessage(String userId, String userName) {
		String welcomeMessage =
			" " + userName + "님, 안녕하세요! 🌸\n" +
				" 저희 서비스는 보호자와 요양보호사를 위한 서비스입니다. \n" +
				" 회원가입을 통해 이용해주시기 바랍니다. 😅";
		lineMessagingUtil.sendMessageToUser(userId, welcomeMessage);
	}
}
