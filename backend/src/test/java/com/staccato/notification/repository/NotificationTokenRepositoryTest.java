package com.staccato.notification.repository;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.notification.domain.NotificationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NotificationTokenRepositoryTest extends RepositoryTest {

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;
    @Autowired
    private MemberRepository memberRepository;
    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
    }

    @DisplayName("사용자의 토큰을 조회할 수 있다.")
    @Test
    void findByMember() {
        // given
        NotificationToken token = new NotificationToken("test-token", member);
        notificationTokenRepository.save(token);

        // when
        Optional<NotificationToken> found = notificationTokenRepository.findByMember(member);

        // then
        assertAll(
                () -> assertThat(found).isPresent(),
                () -> assertThat(found.get().getToken()).isEqualTo("test-token"),
                () -> assertThat(found.get().getMember()).isEqualTo(member)
        );
    }

    @DisplayName("토큰 값을 기반으로 모든 토큰을 삭제할 수 있다.")
    @Test
    void deleteAllByToken() {
        // given
        NotificationToken token = new NotificationToken("token-to-delete", member);
        notificationTokenRepository.save(token);

        // when
        notificationTokenRepository.deleteAllByToken("token-to-delete");

        // then
        Optional<NotificationToken> found = notificationTokenRepository.findByMember(member);
        assertThat(found).isEmpty();
    }
}
