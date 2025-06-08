package com.staccato.notification.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.category.domain.Category;
import com.staccato.comment.domain.Comment;
import com.staccato.config.log.annotation.Trace;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.dto.response.NotificationExistResponse;
import com.staccato.notification.domain.NotificationToken;
import com.staccato.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final CategoryInvitationRepository categoryInvitationRepository;

    public NotificationExistResponse isExistNotifications(Member member) {
        boolean isExist = categoryInvitationRepository.existsByInviteeIdAndStatus(member.getId(), InvitationStatus.REQUESTED);
        return new NotificationExistResponse(isExist);
    private final NotificationTokenRepository notificationTokenRepository;
    private final FcmService fcmService;

    @Transactional
    public void register(Member member, String token) {
        notificationTokenRepository.findByMember(member).ifPresentOrElse(
                notificationToken -> notificationToken.update(token),
                () -> {
                    NotificationToken notificationToken = new NotificationToken(token, member);
                    notificationTokenRepository.save(notificationToken);
                });
    }

    public void sendInvitationAlert(Member sender, List<Member> receivers, Category category) {
        String title = String.format("%s님이 초대를 보냈어요", sender.getNickname().getNickname());
        String message = category.getTitle().getTitle();
        sendToMembers(title, message, receivers);
    }

    public void sendAcceptAlert(Member accepter, Category category, List<Member> existingMembers) {
        String title = String.format("%s님이 참여했어요", accepter.getNickname().getNickname());
        String message = String.format("%s에서 환영해주세요!", category.getTitle().getTitle());
        sendToMembers(title, message, existingMembers);
    }

    public void sendNewStaccatoAlert(Member member, Category category, List<Member> existingMembers) {
        String title = "스타카토가 추가됐어요";
        String message = String.format("%s님이 %s에 남긴 스타카토를 확인해보세요",
                member.getNickname().getNickname(),
                category.getTitle().getTitle());
        sendToMembers(title, message, existingMembers);
    }

    public void sendNewCommentAlert(Member commenter, Comment comment, List<Member> existingMembers) {
        String title = String.format("%s님의 코멘트", commenter.getNickname().getNickname());
        String message = comment.getContent();
        sendToMembers(title, message, existingMembers);
    }

    private void sendToMembers(String title, String message, List<Member> members) {
        List<String> tokens = members.stream()
                .flatMap(m -> notificationTokenRepository.findByMember(m).stream())
                .map(NotificationToken::getToken)
                .toList();
        fcmService.sendPush(tokens, title, message);
    }
}
