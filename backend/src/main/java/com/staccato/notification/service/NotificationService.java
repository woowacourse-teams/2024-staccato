package com.staccato.notification.service;

import java.util.List;
import java.util.Map;

import javax.swing.text.SimpleAttributeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.StaccatoApplication;
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
    private final FcmService fcmService;

    @Transactional(readOnly = true)
    public NotificationExistResponse isExistNotifications(Member member) {
        boolean isExist = categoryInvitationRepository.existsByInviteeIdAndStatus(member.getId(), InvitationStatus.REQUESTED);
        return new NotificationExistResponse(isExist);
    }

    @Transactional
    public void register(Member member, NotificationTokenRequest notificationTokenRequest) {
        notificationTokenRepository.findByMemberIdAndDeviceTypeAndDeviceId(member.getId(), notificationTokenRequest.toDeviceType(), notificationTokenRequest.deviceId())
                .ifPresentOrElse(
                        notificationToken -> notificationToken.updateToken(notificationTokenRequest.token()),
                        () -> {
                            NotificationToken notificationToken = new NotificationToken(
                                    notificationTokenRequest.token(),
                                    member,
                                    notificationTokenRequest.toDeviceType(),
                                    notificationTokenRequest.deviceId());
                            notificationTokenRepository.save(notificationToken);
                        });
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
        fcmService.sendPush(tokens, pushMessage);
    }
}
