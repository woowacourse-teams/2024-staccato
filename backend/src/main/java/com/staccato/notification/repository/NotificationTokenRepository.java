package com.staccato.notification.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.staccato.member.domain.Member;
import com.staccato.notification.domain.NotificationToken;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    Optional<NotificationToken> findByMember(Member member);

    void deleteAllByToken(String token);
}
