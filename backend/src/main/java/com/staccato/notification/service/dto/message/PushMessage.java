package com.staccato.notification.service.dto.message;

import java.util.Map;
import com.google.firebase.messaging.Notification;

public sealed interface PushMessage permits
        AcceptInvitationMessage,
        CommentCreatedMessage,
        ReceiveInvitationMessage,
        StaccatoCreatedMessage {
    Notification toNotification();
    Map<String, String> toData();
    String getTitle();
    String getBody();
}
