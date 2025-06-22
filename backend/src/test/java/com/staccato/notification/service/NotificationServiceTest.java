package com.staccato.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

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

    private Member host;
    private Member guest;
    private Member other;
    private Category category;
    private Staccato staccato;
    private Comment comment;

    @BeforeEach
    void setUp() {
        host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        other = MemberFixtures.defaultMember().withNickname("other").buildAndSave(memberRepository);
        category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);
        staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .buildAndSave(staccatoRepository);
        comment = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(host)
                .buildAndSave(commentRepository);

        NotificationTokenFixtures.defaultNotificationToken(host)
                .withToken("hostToken")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenFixtures.defaultNotificationToken(guest)
                .withToken("guestToken")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenFixtures.defaultNotificationToken(other)
                .withToken("otherToken")
                .buildAndSave(notificationTokenRepository);
    }

    @DisplayName("받은 초대 목록이 존재한다면, 알림이 존재한다.")
    @Test
    void isExistNotifications() {
        // given
        categoryInvitationRepository.save(CategoryInvitation.invite(category, host, other));

        // when
        NotificationExistResponse result = notificationService.isExistNotifications(other);

        // then
        assertThat(result.isExist()).isTrue();
    }

    @DisplayName("받은 초대 목록이 존재하지 않는다면, 알림이 존재하지 않는다.")
    @Test
    void isNotExistNotifications() {
        // when
        NotificationExistResponse result = notificationService.isExistNotifications(other);

        // then
        assertThat(result.isExist()).isFalse();
    }

    @DisplayName("초대 알림을 전송한다.")
    @Test
    void sendInvitationAlert() {
        // when
        notificationService.sendInvitationAlert(host, category, List.of(other));

        // then
        verify(fcmService).sendPush(
                argThat(tokens -> tokens.containsAll(List.of("otherToken"))),
                argThat(data ->
                        "RECEIVE_INVITATION".equals(data.get("type")) &&
                                String.format("%s님이 초대를 보냈어요", host.getNickname().getNickname())
                                        .equals(data.get("title")) &&
                                category.getTitle().getTitle().equals(data.get("body"))
                )
        );
    }

    @DisplayName("초대 수락 알림을 전송한다.")
    @Test
    void sendAcceptAlert() {
        // when
        notificationService.sendAcceptAlert(other, category, List.of(host, guest));

        // then
        verify(fcmService).sendPush(
                argThat(tokens -> tokens.containsAll(List.of("hostToken", "guestToken"))),
                argThat(data ->
                        "ACCEPT_INVITATION".equals(data.get("type")) &&
                                String.format("%s님이 참여했어요", other.getNickname().getNickname())
                                        .equals(data.get("title")) &&
                                String.format("%s에서 환영해주세요!", category.getTitle().getTitle())
                                        .equals(data.get("body")) &&
                                String.valueOf(category.getId()).equals(data.get("categoryId"))
                )
        );
    }

    @DisplayName("새로운 스타카토 알림을 전송한다.")
    @Test
    void sendNewStaccatoAlert() {
        // when
        notificationService.sendNewStaccatoAlert(host, category, staccato, List.of(guest));

        // then
        verify(fcmService).sendPush(
                argThat(tokens -> tokens.containsAll(List.of("guestToken"))),
                argThat(data ->
                        "STACCATO_CREATED".equals(data.get("type")) &&
                                "스타카토가 추가됐어요".equals(data.get("title")) &&
                                String.format("%s님이 %s에 남긴 스타카토를 확인해보세요",
                                        host.getNickname().getNickname(),
                                        category.getTitle().getTitle()).equals(data.get("body")) &&
                                String.valueOf(staccato.getId()).equals(data.get("staccatoId"))
                )
        );
    }

    @DisplayName("새로운 코멘트 알림을 전송한다.")
    @Test
    void sendNewCommentAlert() {
        // when
        notificationService.sendNewCommentAlert(host, comment, staccato, List.of(guest));

        // then
        verify(fcmService).sendPush(
                argThat(tokens -> tokens.containsAll(List.of("guestToken"))),
                argThat(data ->
                        "COMMENT_CREATED".equals(data.get("type")) &&
                                String.format("%s님의 코멘트", host.getNickname().getNickname())
                                        .equals(data.get("title")) &&
                                comment.getContent().equals(data.get("body")) &&
                                String.valueOf(staccato.getId()).equals(data.get("staccatoId"))
                )
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
