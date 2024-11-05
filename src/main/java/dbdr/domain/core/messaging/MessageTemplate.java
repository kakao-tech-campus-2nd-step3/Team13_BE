package dbdr.domain.core.messaging;

public enum MessageTemplate {

	FOLLOW_MESSAGE("안녕하세요! 🌸\n 최고의 요양원 서비스 돌봄다리입니다. 🤗\n 서비스를 시작하려면 전화번호를 다음과 같은 형식으로 입력해주시기 바랍니다. 😄\n 예 : 01012345678"),
	STRANGER_FOLLOW_MESSAGE("%s님, 안녕하세요! 🌸\n 저희 서비스는 보호자와 요양보호사를 위한 서비스입니다. \n 회원가입을 통해 이용해주시기 바랍니다. 😅"),
	ERROR_MESSAGE("입력값이 잘못되었습니다! 😅\n 다시 입력해주세요. 💬"),
	RESERVATION_CONFIRMATION_MESSAGE("감사합니다! 😊\n 입력하신 시간 %s %s시%s에 알림을 보내드릴게요. 💬\n 언제든지 알림 시간을 변경하고 싶으시면 다시 알려주세요!"),
	CAREWORKER_WELCOME_MESSAGE("%s 요양보호사님, 안녕하세요! 🌸\n 최고의 요양원 서비스 돌봄다리입니다. 🤗\n 저희와 함께 해주셔서 정말 감사합니다! 🙏\n 기본적인 알림 시간은 매일 오후 5시로 설정되어있습니다. 😄\n 알림을 받고 싶은 시간을 수정하고 싶으시다면 알려주세요! 💬\n 예 : `오후 7시' 혹은 '오후 7시 30분'"),
	GUARDIAN_WELCOME_MESSAGE("%s 보호자님, 안녕하세요! 🌸\n 최고의 요양원 서비스 돌봄다리입니다. 🤗\n 저희와 함께 해주셔서 정말 감사합니다! 🙏\n 새롭게 작성된 일지 내용을 원하시는 시간에 맞춰 알려드릴 수 있어요. ⏰\n 기본적인 알림 시간은 매일 오전 9시로 설정되어있습니다. 😄\n 알림을 받고 싶은 시간을 수정하고 싶으시다면 알려주세요! 💬\n 예 : `오전 10시' 혹은 '오전 10시 30분'"),
	NO_CHART_MESSAGE("어제 작성된 차트 내용이 없습니다! 😅");

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
