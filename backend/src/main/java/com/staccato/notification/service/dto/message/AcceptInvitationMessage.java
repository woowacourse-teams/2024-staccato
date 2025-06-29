package com.staccato.notification.service.dto.message;

import java.util.Map;
import com.google.firebase.messaging.Notification;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;

public class AcceptInvitationMessage extends PushMessage {
    private final String inviteeName;
    private final String categoryTitle;
    private final String categoryId;

    public AcceptInvitationMessage(Member invitee, Category category) {
        super(MessageType.ACCEPT_INVITATION);
        this.inviteeName = invitee.getNickname().getNickname();
        this.categoryTitle = category.getTitle().getTitle();
        this.categoryId = String.valueOf(category.getId());
    }

    @Override
    protected String getTitle() {
        return String.format("%s님이 참여했어요", inviteeName);
    }

    @Override
    protected String getBody() {
        return String.format("%s에서 환영해주세요!", categoryTitle);
    }

    @Override
    protected Map<String, String> getAdditionalData() {
        return Map.of("categoryId", categoryId);
    }
}
