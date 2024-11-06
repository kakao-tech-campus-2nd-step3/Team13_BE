package dbdr.domain.core.alarm.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import dbdr.domain.core.base.entity.BaseEntity;
import dbdr.domain.core.messaging.MessageChannel;
import dbdr.domain.core.messaging.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private MessageChannel channel;

	@Column(nullable = true)
	private String channelId;

	@Column(nullable = false)
	private String phone;

	@Column(nullable = true)
	private boolean isSend = false;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(nullable = false)
	private Long roleId;

	public Alarm(LocalDateTime alertTime, String message, String phone, Role role, Long roleId) {
		this.alertTime = alertTime;
		this.message = message;
		this.phone = phone;
		this.role = role;
		this.roleId = roleId;
	}

	public Alarm(LocalDateTime alertTime, MessageChannel channel, String channelId, String message, String phone, Role role, Long roleId) {
		this.alertTime = alertTime;
		this.channel = channel;
		this.channelId = channelId;
		this.message = message;
		this.phone = phone;
		this.role = role;
		this.roleId = roleId;
	}
}
