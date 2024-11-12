package dbdr.domain.core.messaging;

public enum MessageTemplate {

	FOLLOW_MESSAGE("안녕하세요! 🌸\n 최고의 요양원 서비스 돌봄다리입니다. 🤗\n 서비스를 시작하려면 전화번호를 다음과 같은 형식으로 입력해주시기 바랍니다. 😄\n 예시 : 01012345678"),
	STRANGER_FOLLOW_MESSAGE("%s님, 안녕하세요! 🌸\n 저희 서비스는 보호자와 요양보호사를 위한 서비스입니다. \n 회원가입을 통해 이용해주시기 바랍니다. 😅"),
	INVALID_PHONE_INPUT_MESSAGE("전화번호 입력값이 잘못되었습니다! 😅\n 다시 입력해주세요. 예시 : 01012345678💬"),
	RESERVATION_CONFIRMATION_MESSAGE("감사합니다! 😊\n 입력하신 시간 %s %s시%s에 알림을 보내드릴게요. 💬\n 언제든지 알림 시간을 변경하고 싶으시면 다시 알려주세요!"),
	CAREWORKER_WELCOME_MESSAGE("%s 요양보호사님, 안녕하세요! 🌸\n 최고의 요양원 서비스 돌봄다리입니다. 🤗\n 저희와 함께 해주셔서 정말 감사합니다! 🙏\n 기본적인 알림 시간은 매일 오후 5시로 설정되어있습니다. 😄\n 돌봄다리 서비스의 마이페이지에서 알림 시간을 수정할 수 있습니다."),
	GUARDIAN_WELCOME_MESSAGE("%s 보호자님, 안녕하세요! 🌸\n 최고의 요양원 서비스 돌봄다리입니다. 🤗\n 저희와 함께 해주셔서 정말 감사합니다! 🙏\n 새롭게 작성된 일지 내용을 원하시는 시간에 맞춰 알려드릴 수 있어요. ⏰\n 기본적인 알림 시간은 매일 오전 9시로 설정되어있습니다. 😄\n 돌봄다리 서비스의 마이페이지에서 알림 시간을 수정할 수 있습니다.\""),
	NO_CHART_MESSAGE("새롭게 작성된 차트 내용이 없습니다! 😅"),
	CAREWORKER_ALARM_MESSAGE("%s 요양보호사님, 오늘 하루는 어떠셨나요? 😊\n이제 차트를 작성하실 시간입니다. 잊지 마시고 차트 작성 부탁드립니다! 📝"),
	CHART_UPDATED_MESSAGE("%s님, 안녕하세요! 😊 새로운 차트 정보가 업데이트되었습니다. 📋\n" +
		"- 상태/질환: %s\n" +
		"- 신체 관리: 세수 - %s, 목욕 - %s, 식사 - %s\n" +
		"- 간호 관리: 혈압 - %s/%s, 체온 - %s도\n" +
		"- 인지 관리: 인지 도움 제공 - %s\n" +
		"- 회복 훈련 프로그램: %s\n\n" +
		"더 자세한 내용은 돌봄다리에서 확인하세요. 감사합니다! 🙏");
	private final String template;

	MessageTemplate(String template) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}

	public String format(Object... args) {
		return String.format(template, args);
	}
}
