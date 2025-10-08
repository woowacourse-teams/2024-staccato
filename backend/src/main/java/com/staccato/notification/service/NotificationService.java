package com.staccato.notification.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.category.domain.Category;
import com.staccato.comment.domain.Comment;
import com.staccato.config.log.annotation.Trace;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;
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

import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final CategoryInvitationRepository categoryInvitationRepository;
    private final NotificationTokenRepository notificationTokenRepository;
    private final PushService pushService;

    @Transactional(readOnly = true)
    public NotificationExistResponse isExistNotifications(Member member) {
        boolean isExist = categoryInvitationRepository.existsByInviteeIdAndStatus(member.getId(), InvitationStatus.REQUESTED);
        return new NotificationExistResponse(isExist);
    }

    @Transactional
    public void register(Member member, NotificationTokenRequest request) {
        Optional<NotificationToken> notificationToken = notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(
                member.getId(), request.toDeviceType(), request.deviceId());
        notificationToken.ifPresentOrElse(
                existingToken -> existingToken.updateToken(request.token()),
                () -> createNotificationToken(member, request)
        );
    }

    private void createNotificationToken(Member member, NotificationTokenRequest request) {
        List<NotificationToken> notificationToken = notificationTokenRepository.findAllByToken(request.token());
        if (notificationToken.size() == 1) {
            NotificationToken existingToken = notificationToken.get(0);
            existingToken.updateDeviceId(request.deviceId());
            return;
        }

        if (notificationToken.size() >= 2) {
            notificationTokenRepository.deleteAllByToken(notificationToken.get(0).getToken());
        }
        notificationTokenRepository.save(new NotificationToken(request.token(), member, request.toDeviceType(), request.deviceId()));
    }

    public void sendInvitationAlert(Member inviter, Category category, List<Member> receivers) {
        ReceiveInvitationMessage message = new ReceiveInvitationMessage(inviter, category);
        sendToMembers(receivers, message);
    }

    public void sendAcceptAlert(Member invitee, Category category, List<Member> receivers) {
        AcceptInvitationMessage message = new AcceptInvitationMessage(invitee, category);
        sendToMembers(receivers, message);
    }

    public void sendNewStaccatoAlert(Member staccatoCreator, Category category, Staccato staccato, List<Member> receivers) {
        StaccatoCreatedMessage message = new StaccatoCreatedMessage(staccatoCreator, staccato, category);
        sendToMembers(receivers, message);
    }

    public void sendNewCommentAlert(Member commentCreator, Comment comment, Staccato staccato, List<Member> receivers) {
        CommentCreatedMessage message = new CommentCreatedMessage(commentCreator, staccato, comment);
        sendToMembers(receivers, message);
    }

    private void sendToMembers(List<Member> members, PushMessage pushMessage) {
        List<NotificationToken> notificationTokens = notificationTokenRepository.findByMemberIn(members);
        List<String> tokens = notificationTokens.stream()
                .map(NotificationToken::getToken)
                .toList();
        pushService.sendPush(tokens, pushMessage);
    }
}
