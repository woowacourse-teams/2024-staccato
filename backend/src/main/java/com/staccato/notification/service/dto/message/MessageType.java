package com.staccato.notification.service.dto.message;

public enum MessageType {
    ACCEPT_INVITATION,
    RECEIVE_INVITATION,
    STACCATO_CREATED,
    COMMENT_CREATED;

    public String type() {
        return name();
    }
}


