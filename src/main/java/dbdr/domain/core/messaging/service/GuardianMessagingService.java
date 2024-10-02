package dbdr.domain.core.messaging.service;

import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.global.util.LineMessagingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuardianMessagingService {

	private final GuardianService guardianService;
	private final GuardianRepository guardianRepository;
	private final LineMessagingUtil lineMessagingUtil;


	@Transactional
	public void saveUserIdByPhoneNumber(String userId, String phoneNumber) {
		Guardian guardian = guardianService.findByPhone(phoneNumber);
		String userName = guardian.getName();
		guardian.updateLineUserId(userId);
		guardianRepository.save(guardian);

		String welcomeMessage =
			" " + userName + " 보호자님, 안녕하세요! 🌸\n" +
				" 최고의 요양원 서비스 돌봄다리입니다. 🤗\n" +
				" 저희와 함께 해주셔서 정말 감사합니다! 🙏\n" +
				" 새롭게 작성된 일지 내용을 원하시는 시간에 맞춰 알려드릴 수 있어요. ⏰\n" +
				" 기본적인 알림 시간은 매일 오전 9시로 설정되어있습니다. 😄\n" +
				" 알림을 받고 싶은 시간을 수정하고 싶으시다면 알려주세요! 💬\n" +
				" 예 : `오전 10시' 혹은 '오전 10시 30분'";

		lineMessagingUtil.sendMessageToUser(userId, welcomeMessage);
	}

	@Transactional
	public void updateGuardianAlertTime(String userId, String ampm, String hour, String minute) {
		Guardian guardian = guardianService.findByLineUserId(userId);
		LocalTime alertTime = lineMessagingUtil.convertToLocalTime(ampm, Integer.parseInt(hour), Integer.parseInt(minute));
		guardian.updateAlertTime(alertTime);
		guardianRepository.save(guardian);
	}


}
