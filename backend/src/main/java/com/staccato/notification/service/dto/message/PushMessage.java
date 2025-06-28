package com.staccato.notification.service.dto.message;

import java.util.Map;

public interface PushMessage {
    Map<String, String> toMap();
    String getTitle();
    String getBody();
}
