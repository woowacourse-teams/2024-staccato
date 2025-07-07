package com.staccato.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import com.staccato.config.domain.BaseEntity;
import com.staccato.member.domain.Member;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "notification_token",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_notification_token_unique",
                columnNames = {"device_type", "device_id", "member_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class NotificationToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String token;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;
    @Column(nullable = false)
    private String deviceId;

    public NotificationToken(String token, Member member, DeviceType deviceType, String deviceId) {
        this.token = token;
        this.member = member;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
