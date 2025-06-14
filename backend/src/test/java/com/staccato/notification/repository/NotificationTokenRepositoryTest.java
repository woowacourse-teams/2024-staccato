package com.staccato.notification.repository;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.notification.NotificationTokenFixtures;
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
        NotificationToken token = NotificationTokenFixtures.defaultNotificationToken(member)
                .buildAndSave(notificationTokenRepository);

        // when
        Optional<NotificationToken> found = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(member.getId(), token.getDeviceType(), token.getDeviceId());

        // then
        assertAll(
                () -> assertThat(found).isPresent(),
                () -> assertThat(found.get().getToken()).isEqualTo(token.getToken()),
                () -> assertThat(found.get().getMember()).isEqualTo(member)
        );
    }

    @DisplayName("토큰 값을 기반으로 모든 토큰을 삭제할 수 있다.")
    @Test
    void deleteAllByToken() {
        // given
        NotificationToken token = NotificationTokenFixtures.defaultNotificationToken(member)
                .withToken("token-to-delete")
                .buildAndSave(notificationTokenRepository);

        // when
        notificationTokenRepository.deleteAllByToken("token-to-delete");

        // then
        Optional<NotificationToken> found = notificationTokenRepository.findById(token.getId());
        assertThat(found).isEmpty();
    }

    @DisplayName("여러 명의 사용자에 대해 모든 토큰을 조회할 수 있다.")
    @Test
    void findByMemberIn() {
        // given
        Member member1 = MemberFixtures.defaultMember().withNickname("user1").buildAndSave(memberRepository);
        Member member2 = MemberFixtures.defaultMember().withNickname("user2").buildAndSave(memberRepository);

        NotificationToken token1 = NotificationTokenFixtures.defaultNotificationToken(member1)
                .withToken("token-1")
                .buildAndSave(notificationTokenRepository);

        NotificationToken token2 = NotificationTokenFixtures.defaultNotificationToken(member2)
                .withToken("token-2")
                .buildAndSave(notificationTokenRepository);

        // when
        List<NotificationToken> tokens = notificationTokenRepository.findByMemberIn(List.of(member1, member2));

        // then
        assertThat(tokens).hasSize(2)
                .extracting(NotificationToken::getToken)
                .containsExactlyInAnyOrder("token-1", "token-2");
    }
}
