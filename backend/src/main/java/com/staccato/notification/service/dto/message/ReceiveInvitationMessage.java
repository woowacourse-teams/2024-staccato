package com.staccato.notification.service.dto.message;

import java.util.Map;

import com.google.firebase.messaging.Notification;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;

public class ReceiveInvitationMessage extends PushMessage {
    private final String inviterName;
    private final String categoryTitle;

    public ReceiveInvitationMessage(Member inviter, Category category) {
        super(MessageType.RECEIVE_INVITATION);
        this.inviterName = inviter.getNickname().getNickname();
        this.categoryTitle = category.getTitle().getTitle();
    }

    @Override
    protected String getTitle() {
        return String.format("%s님이 초대를 보냈어요", inviterName);
    }

    @Override
    protected String getBody() {
        return categoryTitle;
    }
}
