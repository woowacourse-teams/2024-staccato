package com.staccato.notification.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.staccato.member.domain.Member;
import com.staccato.notification.domain.DeviceType;
import com.staccato.notification.domain.NotificationToken;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    Optional<NotificationToken> findByMemberIdAndDeviceTypeAndDeviceId(long memberId, DeviceType deviceType, String deviceId);

    List<NotificationToken> findByMemberIn(List<Member> members);

    void deleteAllByToken(String token);
}
