package com.staccato.notification.service;

import java.util.List;

import com.staccato.notification.service.dto.message.PushMessage;

public interface PushService {
    void sendPush(List<String> tokens, PushMessage pushMessage);
}
