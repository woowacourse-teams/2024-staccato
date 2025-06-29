package com.staccato.notification.service.dto.message;

import java.util.Map;

public sealed interface PushMessage permits
        AcceptInvitationMessage,
        CommentCreatedMessage,
        ReceiveInvitationMessage,
        StaccatoCreatedMessage {
    Map<String, String> toMap();
    String getTitle();
    String getBody();
}
