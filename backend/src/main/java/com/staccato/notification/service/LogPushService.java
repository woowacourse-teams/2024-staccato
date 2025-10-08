package com.staccato.notification.service;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.staccato.config.log.annotation.Trace;
import com.staccato.notification.service.dto.message.PushMessage;

import lombok.extern.slf4j.Slf4j;

@Trace
@Slf4j
@Service
@Profile({"local", "test"})
public class LogPushService implements PushService {

    @Override
    public void sendPush(List<String> tokens, PushMessage pushMessage) {
        int tokenCount = (tokens == null) ? 0 : tokens.size();
        Map<String, String> data = pushMessage.toData();

        log.info("[DRY-PUSH] tokenCount={}, data={}", tokenCount, data);
    }
}
