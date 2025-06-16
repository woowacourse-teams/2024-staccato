package com.staccato.notification.listener;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.service.CommentService;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentRequestFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.fixture.staccato.StaccatoRequestFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.InvitationService;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.notification.service.FcmService;
import com.staccato.notification.service.NotificationService;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;
import com.staccato.staccato.service.StaccatoService;
import com.staccato.staccato.service.dto.request.StaccatoRequest;

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotificationEventListenerTest extends ServiceSliceTest {

    @Autowired
    private StaccatoService staccatoService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private InvitationService invitationService;
    @SpyBean
    private NotificationService notificationService;
    @MockBean
    private FcmService fcmService;

    private Member host;
    private Member guest;
    private Member guest2;
    private Category category;

    @BeforeEach
    void init() {
        host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        guest2 = MemberFixtures.defaultMember().withNickname("guest2").buildAndSave(memberRepository);
        category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest, guest2))
                .buildAndSave(categoryRepository);
    }

    @DisplayName("공유 카테고리에서 Staccato 생성 시 알림을 전송한다.")
    @Test
    void handleNewStaccato() {
        // given
        StaccatoRequest request = StaccatoRequestFixtures.defaultStaccatoRequest()
                .withCategoryId(category.getId())
                .build();

        // when
        staccatoService.createStaccato(request, host);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationService, times(1))
                        .sendNewStaccatoAlert(
                                eq(host),
                                eq(category),
                                argThat(members -> members.containsAll(List.of(guest, guest2)))
                        )
        );
    }

    @DisplayName("공유 카테고리에서 댓글 작성 시 알림을 전송한다.")
    @Test
    void handleNewComment() {
        // given
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .buildAndSave(staccatoRepository);
        CommentRequest commentRequest = CommentRequestFixtures.defaultCommentRequest()
                .withStaccatoId(staccato.getId()).build();

        // when
        commentService.createComment(commentRequest, host);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationService, times(1))
                        .sendNewCommentAlert(eq(host),
                                argThat(comment -> comment.getContent().equals(commentRequest.content())),
                                argThat(m -> m.containsAll(List.of(guest, guest2, host))))
        );
    }

    @DisplayName("카테고리 초대 시 알림을 전송한다.")
    @Test
    void handleInvitation() {
        // given
        Member invitee = MemberFixtures.defaultMember()
                .withNickname("invitee")
                .buildAndSave(memberRepository);
        Member invitee2 = MemberFixtures.defaultMember()
                .withNickname("invitee2")
                .buildAndSave(memberRepository);
        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(invitee.getId(), invitee2.getId()));

        // when
        invitationService.invite(host, invitationRequest);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationService, times(1))
                        .sendInvitationAlert(eq(host), argThat(list -> list.containsAll(List.of(invitee, invitee2))), eq(category))
        );
    }

    @DisplayName("카테고리 초대 수락 시 기존 멤버에게 알림을 전송한다.")
    @Test
    void handleInvitationAccepted() {
        // given
        Member invitee = MemberFixtures.defaultMember()
                .withNickname("invitee")
                .buildAndSave(memberRepository);
        CategoryInvitation categoryInvitation = CategoryInvitation.invite(category, host, invitee);
        categoryInvitationRepository.save(categoryInvitation);

        // when
        invitationService.accept(invitee, categoryInvitation.getId());

        // then
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationService, times(1))
                        .sendAcceptAlert(eq(invitee), eq(category), argThat(m -> m.containsAll(List.of(guest, guest2, host))))
        );
    }
}
