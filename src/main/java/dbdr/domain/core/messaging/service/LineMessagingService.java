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

import dbdr.domain.careworker.service.CareworkerMessagingService;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.guardian.service.GuardianMessagingService;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.global.util.line.LineMessagingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineMessagingService {

	private final ObjectMapper objectMapper;
	private final GuardianMessagingService guardianMessagingService;
	private final CareworkerMessagingService careworkerMessagingService;
	private final GuardianService guardianService;
	private final CareworkerService careworkerService;
	private final LineMessagingUtil lineMessagingUtil;

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
							FollowEvent followEvent = objectMapper.treeToValue(eventNode, FollowEvent.class);
							handleFollowEvent(followEvent);
							break;
						case "message":
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
	// 사용자가 라인 채널을 추가하였을 때 발생하는 이벤트 처리
	@Transactional
	public void handleFollowEvent(FollowEvent event) {
		String userId = event.getSource().getUserId();
		String followMessage = "안녕하세요! 🌸\n" +
			" 최고의 요양원 서비스 돌봄다리입니다. 🤗\n" +
			" 서비스를 시작하려면 전화번호를 다음과 같은 형식으로 입력해주시기 바랍니다. 😄\n" +
			" 예 : 01012345678";
		lineMessagingUtil.sendMessageToUser(userId, followMessage);
	}

	private void sendStrangerFollowMessage(String userId, String userName) {
		String welcomeMessage =
			" " + userName + "님, 안녕하세요! 🌸\n" +
				" 저희 서비스는 보호자와 요양보호사를 위한 서비스입니다. \n" +
				" 회원가입을 통해 이용해주시기 바랍니다. 😅";
		lineMessagingUtil.sendMessageToUser(userId, welcomeMessage);
	}

	// 2. Message Event 처리
	@Transactional
	public void handleMessageEvent(MessageEvent<TextMessageContent> event) {
		String userId = event.getSource().getUserId();
		String messageText = event.getMessage().getText();

		// 전화번호 형식인지 확인
		Pattern phoneNumber = Pattern.compile("01[0-9]{8,9}");
		Matcher matcherPhone = phoneNumber.matcher(messageText);

		// 알림 예약 형식인지 확인
		Pattern reservation = Pattern.compile("(오전|오후)\\s*(\\d{1,2})시(?:\\s*(\\d{1,2})분)?");
		Matcher matcherReservation = reservation.matcher(messageText);

		if (matcherPhone.find()) {
			handlePhoneNumberMessage(userId, matcherPhone.group());
		} else if (matcherReservation.find()) {
			handleReservationMessage(userId, matcherReservation.group(1), matcherReservation.group(2), matcherReservation.group(3));
		} else {
			String errorMessage =
				" 입력값이 잘못되었습니다! 😅\n" +
				" 다시 입력해주세요. 💬\n";
			lineMessagingUtil.sendMessageToUser(userId, errorMessage);
		}
	}


	// 사용자가 전화 번호를 입력했을 때 발생하는 이벤트 처리
	@Transactional
	public void handlePhoneNumberMessage(String userId, String phoneNumber) {
		String userName = lineMessagingUtil.getUserProfile(userId).getDisplayName();

		if (guardianService.findByPhone(phoneNumber) != null) {
			guardianMessagingService.handleGuardianPhoneMessage(userId, phoneNumber);
		} else if (careworkerService.findByPhone(phoneNumber) != null) {
			careworkerMessagingService.handleCareworkerPhoneMessage(userId, phoneNumber);
		} else {
			sendStrangerFollowMessage(userId, userName);
		}
	}

	// 사용자가 알림 예약 메시지를 보냈을 때 발생하는 이벤트 처리
	@Transactional
	public void handleReservationMessage(String userId, String ampm, String hour, String minute) {
		String confirmationMessage =
			" 감사합니다! 😊\n" +
				" 입력하신 시간 " + ampm + " " + hour + "시" + (minute != null ? " " + minute + "분" : "") + "에 알림을 보내드릴게요. 💬\n" +
				" 언제든지 알림 시간을 변경하고 싶으시면 다시 알려주세요!";

		if (guardianService.findByLineUserId(userId) != null) {
			lineMessagingUtil.sendMessageToUser(userId, confirmationMessage);
			guardianMessagingService.updateGuardianAlertTime(userId, ampm, hour, minute);
		} else if (careworkerService.findByLineUserId(userId) != null) {
			lineMessagingUtil.sendMessageToUser(userId, confirmationMessage);
			careworkerMessagingService.updateCareworkerAlertTime(userId, ampm, hour, minute);
		} else {
			lineMessagingUtil.userFoundFailedMessage(userId);
		}
	}
}
