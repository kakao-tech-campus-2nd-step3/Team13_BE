package dbdr.domain.core.messaging.service;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineMessagingService {
	private final LineMessagingClient lineMessagingClient;
	private final ObjectMapper objectMapper;
	private final GuardianService guardianService;
	private final GuardianRepository guardianRepository;
	private final CareworkerService careworkerService;
	private final CareworkerRepository careworkerRepository;

	// 라인 이벤트 처리
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
						default:
							throw new ApplicationException(ApplicationError.CANNOT_FIND_EVENT);
					}
				}
			} else {
				log.warn("events 배열을 찾을 수 없습니다.");
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
		log.info("User ID: {}", userId);

		String userName = getUserProfile(userId).getDisplayName();

		if (guardianService.nameExists(userName)) {
			followedByGuardian(userId, userName);
		} else if(careworkerService.nameExists(userName)) {
			followedByCareworker(userId, userName);
		} else {
			followedByStranger(userId, userName);
		}
	}

	// 보호자가 라인 채널을 추가하였을 때 발생하는 이벤트 처리 (라인 UserID를 업데이트 후 저장)
	@Transactional
	public void followedByGuardian(String userId, String userName) {
		log.info("보호자 이름: {}", userName);
		Guardian guardian = guardianService.findByName(userName);
		guardian.updateLineUserId(userId);
		guardianRepository.save(guardian);

		String welcomeMessage =
				" " + userName + " 보호자님, 안녕하세요! 🌸\n" +
				" 최고의 요양원 서비스 돌봄다리입니다. 🤗\n" +
				" 저희와 함께 해주셔서 정말 감사합니다! 🙏\n" +
				" 새롭게 작성된 차트 내용을 원하시는 시간에 맞춰 알려드릴 수 있어요. ⏰\n" +
				" 알림을 받고 싶은 시간을 알려주세요! 💬 예: 오후 9시 ";

		sendMessageToUser(userId, welcomeMessage);
	}

	// 요양보호사가 라인 채널을 추가하였을 때 발생하는 이벤트 처리 (라인 UserID를 업데이트 후 저장)
	@Transactional
	public void followedByCareworker(String userId, String userName) {
		log.info("요양보호사 이름: {}", userName);
		Careworker careworker = careworkerService.findByName(userName);
		careworker.updateLineUserId(userId);
		careworkerRepository.save(careworker);

		String welcomeMessage =
			" " + userName + " 요양보호사님, 안녕하세요! 🌸 \n" +
				" 최고의 요양원 서비스 돌봄다리입니다. 🤗\n" +
				" 저희와 함께 해주셔서 정말 감사합니다! 🙏\n" +
				" 차트 작성 알림을 받고 싶은 시간을 알려주세요! 예: 오후 9시 💬";

		sendMessageToUser(userId, welcomeMessage);
	}

	// 라인 채널을 추가한 사용자가 보호자나 요양보호사가 아닐 경우
	private void followedByStranger(String userId, String userName) {
		log.info("라인 채널을 추가한 사용자 이름: {}", userName);

		String welcomeMessage =
			" " + userName + "님, 안녕하세요! 🌸\n" +
				" 최고의 요양원 서비스 돌봄다리입니다. 🤗\n" +
				" 저희 서비스는 보호자와 요양보호사를 위한 서비스입니다. \n" +
				" 알림 기능을 이용하시려면 요양원을 통해 회원가입해주시기 바랍니다. 😅\n";

		sendMessageToUser(userId, welcomeMessage);
	}

	// 2. Message Event 처리
	// 사용자가 알림 예약 메시지를 보냈을 때 발생하는 이벤트 처리
	@Transactional
	public void handleMessageEvent(MessageEvent<TextMessageContent> event) {
		String userId = event.getSource().getUserId();
		String messageText = event.getMessage().getText();  // 사용자가 입력한 알림 시간 예: 오후 9시

		log.info("User ID: {}, 알림 시간: {}", userId, messageText);

		Pattern pattern = Pattern.compile("(오전|오후)\\s*(\\d{1,2})");
		Matcher matcher = pattern.matcher(messageText);

		if (matcher.find()) {
			String ampm = matcher.group(1);
			String hour = matcher.group(2);


			log.info("추출된 시ㅏ간 : {}, {}", ampm, hour);

			String confirmationMessage =
				"감사합니다! 😊\n" +
					"입력하신 시간 " + messageText + "에 알림을 보내드릴게요. 💬\n" +
					"언제든지 알림 시간을 변경하고 싶으시면 다시 알려주세요!";

			if (guardianService.findByLineUserId(userId) != null) {
				sendMessageToUser(userId, confirmationMessage);
				saveGuardianAlertTime(userId, ampm, hour);
			} else if (careworkerService.findByLineUserId(userId) != null) {
				sendMessageToUser(userId, confirmationMessage);
				saveCareworkerAlertTime(userId, ampm, hour);
			} else {
				throw new ApplicationException(ApplicationError.USER_NOT_FOUND);
			}
		} else {
			String errorMessage = "알림 시간을 인식할 수 없습니다. '오전 9시' 또는 '오후 3시'와 같은 형식으로 입력해주세요!";
			sendMessageToUser(userId, errorMessage);
		}
	}

	// +) 기타
	// DB에 알림 시간을 저장하는 메서드
	@Transactional
	public void saveGuardianAlertTime(String userId, String ampm, String hour) {
		Guardian guardian = guardianService.findByLineUserId(userId);
		LocalTime alertTime = convertToLocalTime(ampm, Integer.parseInt(hour));
		guardian.updateAlertTime(alertTime);
		guardianRepository.save(guardian);
	}

	@Transactional
	public void saveCareworkerAlertTime(String userId, String ampm, String hour) {
		Careworker careworker = careworkerService.findByLineUserId(userId);
		LocalTime alertTime = convertToLocalTime(ampm, Integer.parseInt(hour));
		careworker.updateAlertTime(alertTime);
		careworkerRepository.save(careworker);
	}

	// UserId를 통해 사용자 이름 가져오는 메서드
	public UserProfileResponse getUserProfile(String userId) {
		try {
			return lineMessagingClient.getProfile(userId).get();
		} catch (Exception e) {
			log.error("Failed to get user profile: {}", userId, e);
			throw new RuntimeException("Failed to get user profile.", e);
		}
	}

	// AM/PM 및 시간을 LocalTime으로 변환하는 메서드
	private LocalTime convertToLocalTime(String ampm, int hour) {
		if (ampm.equalsIgnoreCase("오후") && hour != 12) {
			hour += 12;
		} else if (ampm.equalsIgnoreCase("오전") && hour == 12) {
			hour = 0;  // 오전 12시는 0시로 변환
		}
		return LocalTime.of(hour, 0);  // 시간에 0분 설정
	}

	// 사용자에게 메시지를 보내는 메서드
	public void sendMessageToUser(String userId, String message) {
		TextMessage textMessage = new TextMessage(message);
		PushMessage pushMessage = new PushMessage(userId, textMessage);

		try {
			lineMessagingClient.pushMessage(pushMessage).get();
			log.info("Message sent successfully to user: {}", userId);
		} catch (Exception e) {
			log.error("Failed to send message to user: {}", userId, e);
			throw new RuntimeException("Failed to send message.", e);
		}
	}
}
