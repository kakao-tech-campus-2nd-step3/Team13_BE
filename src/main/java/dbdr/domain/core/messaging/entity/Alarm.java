package dbdr.domain.core.messaging.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.core.messaging.MessageTemplate;
import dbdr.domain.recipient.entity.Recipient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alarms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE alarms SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Alarm extends BaseEntity {
	@Column(nullable = false)
	private LocalDateTime alertTime;

	@Column(nullable = false)
	private String message = MessageTemplate.NO_CHART_MESSAGE.getTemplate();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MessageChannel channel;

	@Column(nullable = true)
	private String channelId;

	@Column(nullable = false)
	private String phone;

	@Column(nullable = true)
	private boolean isSend = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "careworker_id")
	private Careworker careworker;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipient_id")
	private Recipient recipient;

	// 알림을 보냈다고 표시하고 다음 알림을 생성하는 메서드
	public Alarm markAsSentAndCreateNext() {
		this.isSend = true;

		// 다음 날 알림 생성
		Alarm nextAlarm = new Alarm();
		nextAlarm.setAlertTime(this.alertTime.plusDays(1)); // 다음 날 같은 시간으로 설정
		nextAlarm.setMessage(MessageTemplate.NO_CHART_MESSAGE.getTemplate());
		nextAlarm.setChannel(this.channel);
		nextAlarm.setChannelId(this.channelId);
		nextAlarm.setPhone(this.phone);
		nextAlarm.setCareworker(this.careworker);
		nextAlarm.setRecipient(this.recipient);
		nextAlarm.setSend(false); // 새로 생성한 알림의 isSend는 false로 설정

		return nextAlarm;
	}
}
