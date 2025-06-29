package com.staccato.notification.service.dto.message;

import java.util.Map;
import com.google.firebase.messaging.Notification;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public class StaccatoCreatedMessage extends PushMessage {
    private final String staccatoId;
    private final String staccatoCreatorName;
    private final String categoryTitle;

    public StaccatoCreatedMessage(Member creator, Staccato staccato, Category category) {
        super(MessageType.STACCATO_CREATED);
        this.staccatoCreatorName = creator.getNickname().getNickname();
        this.categoryTitle = category.getTitle().getTitle();
        this.staccatoId = String.valueOf(staccato.getId());
    }

    @Override
    protected String getTitle() {
        return "스타카토가 추가됐어요";
    }

    @Override
    protected String getBody() {
        return String.format("%s님이 %s에 남긴 스타카토를 확인해보세요", staccatoCreatorName, categoryTitle);
    }

    @Override
    protected Map<String, String> getAdditionalData() {
        return Map.of("staccatoId", staccatoId);
    }
}
