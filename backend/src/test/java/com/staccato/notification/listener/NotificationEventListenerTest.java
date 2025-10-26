package com.staccato.notification.listener;

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
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
import com.staccato.notification.service.NotificationService;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;
import com.staccato.staccato.service.StaccatoService;
import com.staccato.staccato.service.dto.request.StaccatoRequest;

class NotificationEventListenerTest extends ServiceSliceTest {

    @Autowired
    private StaccatoService staccatoService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private CommentRepository commentRepository;
    @SpyBean
    private NotificationService notificationService;

    @DisplayName("공동카테고리에서 Staccato 생성 시 스타카토 생성자를 제외한 함께하는 사람들 모두에게 알림을 전송한다.")
    @Test
    void handleNewStaccato() {
        // given
        Member host = MemberFixtures.ofDefault()
                .withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault()
                .withNickname("guest").buildAndSave(memberRepository);
        Member staccatoCreator = MemberFixtures.ofDefault()
                .withNickname("creator").buildAndSave(memberRepository);

        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest, staccatoCreator))
                .buildAndSave(categoryRepository);

        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withCategoryId(category.getId()).build();

        // when
        Long staccatoId = staccatoService.createStaccato(staccatoRequest, staccatoCreator).staccatoId();

        // then
        Staccato staccato = staccatoRepository.findById(staccatoId).orElseThrow();

        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationService, times(1))
                        .sendNewStaccatoAlert(
                                eq(staccatoCreator),
                                eq(category),
                                eq(staccato),
                                argThat(targets ->
                                        targets.size() == 2 &&
                                                !targets.contains(staccatoCreator) &&
                                                targets.containsAll(List.of(host, guest))
                                )
                        )
        );
    }

    @DisplayName("공동카테고리에서 댓글 작성 시 댓글 생성자를 제외한 함께하는 사람들 모두에게 알림을 전송한다.")
    @Test
    void handleNewComment() {
        // given
        Member host = MemberFixtures.ofDefault()
                .withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault()
                .withNickname("guest").buildAndSave(memberRepository);
        Member commentCreator = MemberFixtures.ofDefault()
                .withNickname("creator").buildAndSave(memberRepository);

        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest, commentCreator))
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category)
                .buildAndSave(staccatoRepository);

        CommentRequest commentRequest = CommentRequestFixtures.ofDefault()
                .withStaccatoId(staccato.getId()).build();

        // when
        Long commentId = commentService.createComment(commentRequest, commentCreator);

        // then
        Comment comment = commentRepository.findById(commentId).get();

        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationService, times(1))
                        .sendNewCommentAlert(
                                eq(commentCreator),
                                eq(comment),
                                eq(staccato),
                                argThat(targets ->
                                        targets.size() == 2 &&
                                                !targets.contains(commentCreator) &&
                                                targets.containsAll(List.of(host, guest))
                                )
                        )
        );
    }

    @DisplayName("공동카테고리 초대 시 초대받은 사람들에게 알림을 전송한다.")
    @Test
    void handleInvitation() {
        // given
        Member host = MemberFixtures.ofDefault()
                .withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault()
                .withNickname("guest").buildAndSave(memberRepository);
        Member invitee1 = MemberFixtures.ofDefault()
                .withNickname("invitee1").buildAndSave(memberRepository);
        Member invitee2 = MemberFixtures.ofDefault()
                .withNickname("invitee2").buildAndSave(memberRepository);

        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(invitee1.getId(), invitee2.getId()));

        // when
        invitationService.invite(host, invitationRequest);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationService, times(1))
                        .sendInvitationAlert(
                                eq(host),
                                eq(category),
                                argThat(targets ->
                                        targets.size() == 2 &&
                                                targets.containsAll(List.of(invitee1, invitee2)))
                        )
        );
    }

    @DisplayName("카테고리 초대 수락 시 초대 수락자를 제외한 함께하는 사람들 모두에게 알림을 전송한다.")
    @Test
    void handleInvitationAccepted() {
        // given
        Member host = MemberFixtures.ofDefault()
                .withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault()
                .withNickname("guest").buildAndSave(memberRepository);
        Member invitee = MemberFixtures.ofDefault()
                .withNickname("invitee").buildAndSave(memberRepository);

        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        CategoryInvitation categoryInvitation = CategoryInvitation.invite(category, host, invitee);
        categoryInvitationRepository.save(categoryInvitation);

        // when
        invitationService.accept(invitee, categoryInvitation.getId());

        // then
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationService, times(1))
                        .sendAcceptAlert(
                                eq(invitee),
                                eq(category),
                                argThat(targets ->
                                        targets.size() == 2 &&
                                                !targets.contains(invitee) &&
                                                targets.containsAll(List.of(host, guest))
                                )
                        )
        );
    }
}
