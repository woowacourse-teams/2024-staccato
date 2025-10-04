package com.staccato.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import com.staccato.notification.service.dto.message.AcceptInvitationMessage;
import com.staccato.notification.service.dto.message.CommentCreatedMessage;
import com.staccato.notification.service.dto.message.PushMessage;
import com.staccato.notification.service.dto.message.ReceiveInvitationMessage;
import com.staccato.notification.service.dto.message.StaccatoCreatedMessage;
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
        host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        other = MemberFixtures.ofDefault().withNickname("other").buildAndSave(memberRepository);
        category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);
        staccato = StaccatoFixtures.ofDefault(category)
                .buildAndSave(staccatoRepository);
        comment = CommentFixtures.ofDefault(staccato, host)
                .buildAndSave(commentRepository);

        NotificationTokenFixtures.ofDefault(host)
                .withToken("hostToken")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenFixtures.ofDefault(guest)
                .withToken("guestToken")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenFixtures.ofDefault(other)
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
        List<String> expectedTokens = List.of("otherToken");
        PushMessage expectedMessage = new ReceiveInvitationMessage(host, category);

        verify(fcmService).sendPush(expectedTokens, expectedMessage);
    }

    @DisplayName("초대 수락 알림을 전송한다.")
    @Test
    void sendAcceptAlert() {
        // when
        notificationService.sendAcceptAlert(other, category, List.of(host, guest));

        // then
        List<String> expectedTokens = List.of("hostToken", "guestToken");
        PushMessage expectedMessage = new AcceptInvitationMessage(other, category);

        verify(fcmService).sendPush(expectedTokens, expectedMessage);
    }

    @DisplayName("새로운 스타카토 알림을 전송한다.")
    @Test
    void sendNewStaccatoAlert() {
        // when
        notificationService.sendNewStaccatoAlert(host, category, staccato, List.of(guest));

        // then
        List<String> expectedTokens = List.of("guestToken");
        PushMessage expectedMessage = new StaccatoCreatedMessage(host, staccato, category);

        verify(fcmService).sendPush(expectedTokens, expectedMessage);
    }

    @DisplayName("새로운 코멘트 알림을 전송한다.")
    @Test
    void sendNewCommentAlert() {
        // when
        notificationService.sendNewCommentAlert(host, comment, staccato, List.of(guest));

        // then
        List<String> expectedTokens = List.of("guestToken");
        PushMessage expectedMessage = new CommentCreatedMessage(host, staccato, comment);

        verify(fcmService).sendPush(expectedTokens, expectedMessage);
    }

    @DisplayName("토큰이 존재하지 않으면 새로 저장한다.")
    @Test
    void registerNewToken() {
        // given
        Member member = MemberFixtures.ofDefault().withNickname("newbie").buildAndSave(memberRepository);
        NotificationToken notificationToken = NotificationTokenFixtures.ofDefault(member).build();
        NotificationTokenRequest notificationTokenRequest = new NotificationTokenRequest(
                notificationToken.getToken(),
                notificationToken.getDeviceType().getName(),
                notificationToken.getDeviceId());

        // when
        Optional<NotificationToken> beforeRegister = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(
                member.getId(), notificationToken.getDeviceType(), notificationToken.getDeviceId());
        notificationService.register(member, notificationTokenRequest);
        Optional<NotificationToken> afterRegister = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(
                member.getId(), notificationToken.getDeviceType(), notificationToken.getDeviceId());

        // then
        assertAll(
                () -> assertThat(beforeRegister.isPresent()).isFalse(),
                () -> assertThat(afterRegister.isPresent()).isTrue(),
                () -> assertThat(afterRegister.get().getToken()).isEqualTo(notificationToken.getToken())
        );
    }

    @DisplayName("(MemberId, DeviceType, DeviceId)가 동일한 토큰이 존재하면 기존 토큰 값을 갱신한다.")
    @Test
    void updateTokenValueWhenSameTokenExists() {
        // given
        Member member = MemberFixtures.ofDefault().withNickname("member").buildAndSave(memberRepository);
        NotificationToken oldToken = NotificationTokenFixtures.ofDefault(member)
                .withToken("old-token").buildAndSave(notificationTokenRepository);
        NotificationTokenRequest notificationTokenRequest = new NotificationTokenRequest("updated-token", oldToken.getDeviceType()
                .getName(), oldToken.getDeviceId());

        // when
        Optional<NotificationToken> beforeRegister = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(
                member.getId(), oldToken.getDeviceType(), oldToken.getDeviceId());
        notificationService.register(member, notificationTokenRequest);
        Optional<NotificationToken> afterRegister = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(
                member.getId(), oldToken.getDeviceType(), oldToken.getDeviceId());

        // then
        assertAll(
                () -> assertThat(beforeRegister.get().getToken()).isEqualTo(oldToken.getToken()),
                () -> assertThat(afterRegister.get().getToken()).isEqualTo(notificationTokenRequest.token())
        );
    }

    @DisplayName("토큰 값이 동일한 토큰이 정확히 한 개 존재하면 기존 토큰의 DeviceId 값을 갱신한다.")
    @Test
    void updateDeviceIdWhenSameTokenValueExistsOnce() {
        // given
        Member member = MemberFixtures.ofDefault().withNickname("member").buildAndSave(memberRepository);
        String tokenValue = "token-value";
        NotificationTokenFixtures.ofDefault(member)
                .withToken(tokenValue)
                .withDeviceId("old-device-id")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenRequest notificationTokenRequest = new NotificationTokenRequest(
                tokenValue, "android", "new-device-id");

        // when
        List<NotificationToken> beforeRegister = notificationTokenRepository.findAllByToken(tokenValue);
        notificationService.register(member, notificationTokenRequest);
        List<NotificationToken> afterRegister = notificationTokenRepository.findAllByToken(tokenValue);

        assertAll(
                () -> assertThat(beforeRegister).hasSize(1),
                () -> assertThat(afterRegister).hasSize(1),
                () -> assertThat(afterRegister.get(0).getId()).isEqualTo(beforeRegister.get(0).getId()),
                () -> assertThat(afterRegister.get(0).getDeviceId()).isEqualTo("new-device-id")
        );
    }

    @DisplayName("토큰 값이 동일한 토큰이 두 개 이상 존재하면 모든 토큰을 삭제하고 새로운 토큰을 생성한다.")
    @Test
    void deleteAllAndSaveOneWhenSameTokenValueExistsMoreThanTwice() {
        // given
        Member member = MemberFixtures.ofDefault().withNickname("member").buildAndSave(memberRepository);
        String tokenValue = "token-value";
        NotificationTokenFixtures.ofDefault(member)
                .withToken(tokenValue)
                .withDeviceId("old-device-id1")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenFixtures.ofDefault(member)
                .withToken(tokenValue)
                .withDeviceId("old-device-id2")
                .buildAndSave(notificationTokenRepository);
        NotificationTokenRequest notificationTokenRequest = new NotificationTokenRequest(
                tokenValue, "android", "new-device-id");

        // when
        List<NotificationToken> beforeRegister = notificationTokenRepository.findAllByToken(tokenValue);
        notificationService.register(member, notificationTokenRequest);
        List<NotificationToken> afterRegister = notificationTokenRepository.findAllByToken(tokenValue);

        assertAll(
                () -> assertThat(beforeRegister).hasSize(2),
                () -> assertThat(afterRegister).hasSize(1),
                () -> assertThat(notificationTokenRepository.findById(beforeRegister.get(0).getId())).isEmpty(),
                () -> assertThat(notificationTokenRepository.findById(beforeRegister.get(1).getId())).isEmpty(),
                () -> assertThat(afterRegister.get(0).getDeviceId()).isEqualTo("new-device-id")
        );
    }
}
