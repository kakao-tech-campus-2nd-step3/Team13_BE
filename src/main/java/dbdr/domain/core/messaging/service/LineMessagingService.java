package dbdr.domain.core.messaging.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineMessagingService {
	private final LineMessagingClient lineMessagingClient;
	private final ObjectMapper objectMapper;

	// 라인 이벤트 처리
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
							log.warn("알 수 없는 이벤트 타입: {}", eventType);
					}
				}
			} else {
				log.warn("events 배열을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			log.error("이벤트 처리 중 오류 발생", e);
		}
	}

	// 사용자가 라인 채널을 추가하였을 때 발생하는 이벤트 처리
	private void handleFollowEvent(FollowEvent event) {
		String userId = event.getSource().getUserId();
		log.info("User ID: {}", userId);

		String welcomeMessage =
			"안녕하세요! 🌸 최고의 요양원 서비스 돌봄다리입니다. 🤗\n" +
			"저희와 함께 해주셔서 감사합니다! 🙏\n" +
			"새롭게 작성된 차트 내용을 원하시는 시간에 맞춰 알려드릴 수 있어요. ⏰\n" +
			"알림을 받고 싶은 시간을 알려주세요! 예: 오후 9시 💬";
		sendMessage(userId, welcomeMessage);
	}

	// 사용자가 메시지를 보냈을 때 발생하는 이벤트 처리
	private void handleMessageEvent(MessageEvent<TextMessageContent> event) {
		String userId = event.getSource().getUserId();
		String messageText = event.getMessage().getText();  // 사용자가 입력한 알림 시간 예: 오후 9시

		log.info("User ID: {}, 알림 시간: {}", userId, messageText);

		String confirmationMessage =
			"감사합니다! 😊\n" +
			"입력하신 시간 " + messageText + "에 알림을 보내드릴게요. 💬\n" +
			"언제든지 알림 시간을 변경하고 싶으시면 다시 알려주세요!";
		sendMessage(userId, confirmationMessage);
	}


	// 사용자에게 메시지를 보내는 메서드
	public void sendMessage(String userId, String message) {
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
