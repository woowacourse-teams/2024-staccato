package com.staccato.notification.service.dto.message;

import java.util.Map;
import com.google.firebase.messaging.Notification;

public abstract class PushMessage {
    private final MessageType messageType;

    protected PushMessage(MessageType messageType) {
        this.messageType = messageType;
    }

    public Notification toNotification() {
        return Notification.builder()
                .setTitle(getTitle())
                .setBody(getBody())
                .build();
    }

    public Map<String, String> toData() {
        var data = new java.util.HashMap<String, String>();
        data.put("title", getTitle());
        data.put("body", getBody());
        data.put("type", messageType.type());
        data.putAll(getAdditionalData());
        return data;
    }

    protected abstract String getTitle();

    protected abstract String getBody();

    protected Map<String, String> getAdditionalData() {
        return Map.of();
    }
}
