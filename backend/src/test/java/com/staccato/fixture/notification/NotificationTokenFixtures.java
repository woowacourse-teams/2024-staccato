package com.staccato.fixture.notification;

import com.staccato.member.domain.Member;
import com.staccato.notification.domain.DeviceType;
import com.staccato.notification.domain.NotificationToken;
import com.staccato.notification.repository.NotificationTokenRepository;

public class NotificationTokenFixtures {

    public static NotificationTokenBuilder defaultNotificationToken(Member member) {
        return new NotificationTokenBuilder()
                .withToken("default-token")
                .withMember(member)
                .withDeviceType(DeviceType.ANDROID)
                .withDeviceId("default-device-id");
    }

    public static class NotificationTokenBuilder {
        private String token;
        private Member member;
        private DeviceType deviceType;
        private String deviceId;

        public NotificationTokenBuilder withToken(String token) {
            this.token = token;
            return this;
        }

        public NotificationTokenBuilder withMember(Member member) {
            this.member = member;
            return this;
        }

        public NotificationTokenBuilder withDeviceType(DeviceType deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public NotificationTokenBuilder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public NotificationToken build() {
            return new NotificationToken(token, member, deviceType, deviceId);
        }

        public NotificationToken buildAndSave(NotificationTokenRepository repository) {
            return repository.save(build());
        }
    }
}
