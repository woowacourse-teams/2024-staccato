package com.staccato.notification.listener;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.comment.service.dto.event.CommentCreatedEvent;
import com.staccato.invitation.service.dto.event.CategoryInvitationAcceptedEvent;
import com.staccato.invitation.service.dto.event.CategoryInvitationEvent;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.NotificationService;
import com.staccato.staccato.service.dto.event.StaccatoCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final CategoryMemberRepository categoryMemberRepository;
    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvitationAccepted(CategoryInvitationAcceptedEvent event) {
        List<Member> targetMembers = getTargetMembers(event.category(), event.invitee());
        notificationService.sendAcceptAlert(event.invitee(), event.category(), targetMembers);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvitation(CategoryInvitationEvent event) {
        List<Member> targetMembers = event.invitees();
        notificationService.sendInvitationAlert(event.inviter(), event.category(), targetMembers);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNewStaccato(StaccatoCreatedEvent event) {
        Category category = event.category();
        if (category.getIsShared()) {
            List<Member> targetMembers = getTargetMembers(event.category(), event.creator());
            notificationService.sendNewStaccatoAlert(event.creator(), event.category(), event.staccato(), targetMembers);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNewComment(CommentCreatedEvent event) {
        Category category = event.category();
        if (category.getIsShared()) {
            List<Member> targetMembers = getTargetMembers(event.category(), event.commenter());
            notificationService.sendNewCommentAlert(event.commenter(), event.comment(), event.staccato(), targetMembers);
        }
    }

    private List<Member> getTargetMembers(Category category, Member excludeMember) {
        List<Member> members = categoryMemberRepository.findAllMembersByCategoryId(category.getId());
        members.remove(excludeMember);
        return members;
    }
}
