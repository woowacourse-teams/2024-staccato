package com.staccato.notification.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.notification.NotificationTokenFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.notification.domain.NotificationToken;
import com.staccato.notification.repository.NotificationTokenRepository;
import com.staccato.notification.service.dto.request.NotificationTokenRequest;
import com.staccato.notification.service.dto.response.NotificationExistResponse;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class NotificationServiceTest extends ServiceSliceTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NotificationTokenRepository notificationTokenRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;
    @MockBean
    private FcmService fcmService;

    private Member sender;
    private Member receiver1;
    private Member receiver2;
    private Category category;

    @BeforeEach
    void setUp() {
        sender = MemberFixtures.defaultMember().withNickname("sender").buildAndSave(memberRepository);
        receiver1 = MemberFixtures.defaultMember().withNickname("receiver1").buildAndSave(memberRepository);
        receiver2 = MemberFixtures.defaultMember().withNickname("receiver2").buildAndSave(memberRepository);
        category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);

        NotificationTokenFixtures.defaultNotificationToken(sender)
                .withToken("senderToken")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenFixtures.defaultNotificationToken(receiver1)
                .withToken("token1")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenFixtures.defaultNotificationToken(receiver2)
                .withToken("token2")
                .buildAndSave(notificationTokenRepository);
    }

    @DisplayName("받은 초대 목록이 존재한다면, 알림이 존재한다.")
    @Test
    void isExistNotifications() {
        // given
        categoryInvitationRepository.save(CategoryInvitation.invite(category, sender, receiver1));

        // when
        NotificationExistResponse result = notificationService.isExistNotifications(receiver1);

        // then
        assertThat(result.isExist()).isTrue();
    }

    @DisplayName("받은 초대 목록이 존재하지 않는다면, 알림이 존재하지 않는다.")
    @Test
    void isNotExistNotifications() {
        // when
        NotificationExistResponse result = notificationService.isExistNotifications(receiver1);

        // then
        assertThat(result.isExist()).isFalse();
    }

    @DisplayName("초대 알림을 전송한다.")
    @Test
    void sendInvitationAlert() {
        // when
        notificationService.sendInvitationAlert(sender, List.of(receiver1, receiver2), category);

        // then
        verify(fcmService).sendPush(argThat(tokens -> tokens.containsAll(List.of("token1", "token2"))),
                eq("sender님이 초대를 보냈어요"),
                eq(category.getTitle().getTitle())
        );
    }

    @DisplayName("초대 수락 알림을 전송한다.")
    @Test
    void sendAcceptAlert() {
        // when
        notificationService.sendAcceptAlert(receiver1, category, List.of(sender, receiver2));

        // then
        verify(fcmService).sendPush(
                argThat(tokens -> tokens.containsAll(List.of("senderToken", "token2"))),
                eq("receiver1님이 참여했어요"),
                eq(category.getTitle().getTitle() + "에서 환영해주세요!")
        );
    }

    @DisplayName("새로운 스타카토 알림을 전송한다.")
    @Test
    void sendNewStaccatoAlert() {
        // when
        notificationService.sendNewStaccatoAlert(sender, category, List.of(receiver1, receiver2));

        // then
        verify(fcmService).sendPush(
                argThat(tokens -> tokens.containsAll(List.of("token1", "token2"))),
                eq("스타카토가 추가됐어요"),
                eq("sender님이 " + category.getTitle().getTitle() + "에 남긴 스타카토를 확인해보세요")
        );
    }

    @DisplayName("새로운 댓글 알림을 전송한다.")
    @Test
    void sendNewCommentAlert() {
        // given
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .buildAndSave(staccatoRepository);
        Comment comment = CommentFixtures.defaultComment()
                .withMember(sender)
                .withStaccato(staccato)
                .buildAndSave(commentRepository);

        // when
        notificationService.sendNewCommentAlert(sender, comment, List.of(receiver1, receiver2));

        // then
        verify(fcmService).sendPush(
                argThat(tokens -> tokens.containsAll(List.of("token1", "token2"))),
                eq("sender님의 코멘트"),
                eq(comment.getContent())
        );
    }

    @DisplayName("토큰이 존재하지 않으면 새로 저장한다.")
    @Test
    void registerNewToken() {
        // given
        Member member = MemberFixtures.defaultMember().withNickname("newbie").buildAndSave(memberRepository);
        NotificationToken notificationToken = NotificationTokenFixtures.defaultNotificationToken(member).build();
        NotificationTokenRequest notificationTokenRequest = new NotificationTokenRequest(
                notificationToken.getToken(),
                notificationToken.getDeviceType().getName(),
                notificationToken.getDeviceId());

        // when
        Optional<NotificationToken> beforeRegister = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(member.getId(), notificationToken.getDeviceType(), notificationToken.getDeviceId());
        notificationService.register(member, notificationTokenRequest);
        Optional<NotificationToken> afterRegister = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(member.getId(), notificationToken.getDeviceType(), notificationToken.getDeviceId());

        // then
        assertAll(
                () -> assertThat(beforeRegister.isPresent()).isFalse(),
                () -> assertThat(afterRegister.isPresent()).isTrue(),
                () -> assertThat(afterRegister.get().getToken()).isEqualTo(notificationToken.getToken())
        );
    }

    @DisplayName("이미 토큰이 존재하면 기존 토큰을 갱신한다.")
    @Test
    void registerAndUpdateToken() {
        // given
        Member member = MemberFixtures.defaultMember().withNickname("reuser").buildAndSave(memberRepository);
        NotificationToken oldToken = NotificationTokenFixtures.defaultNotificationToken(member)
                .withToken("old-token").buildAndSave(notificationTokenRepository);
        NotificationTokenRequest notificationTokenRequest = new NotificationTokenRequest("updated-token", oldToken.getDeviceType()
                .getName(), oldToken.getDeviceId());

        // when
        Optional<NotificationToken> beforeRegister = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(member.getId(), oldToken.getDeviceType(), oldToken.getDeviceId());
        notificationService.register(member, notificationTokenRequest);
        Optional<NotificationToken> afterRegister = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(member.getId(), oldToken.getDeviceType(), oldToken.getDeviceId());

        // then
        assertAll(
                () -> assertThat(beforeRegister.get().getToken()).isEqualTo(oldToken.getToken()),
                () -> assertThat(afterRegister.get().getToken()).isEqualTo(notificationTokenRequest.token())
        );
    }
}
