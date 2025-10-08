package com.staccato.notification.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import com.staccato.config.log.annotation.Trace;
import com.staccato.notification.repository.NotificationTokenRepository;
import com.staccato.notification.service.dto.message.PushMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Trace
@Slf4j
@Service
@RequiredArgsConstructor
@Profile({"prod", "dev"})
public class FcmPushService implements PushService {
    private static final int FCM_MULTICAST_LIMIT = 500;
    private static final String SEND_SUCCESS_LOG = "[FCM][전송 완료] ";
    private static final String SEND_FAIL_LOG = "[FCM][전송 실패] ";
    private static final String TOKEN_DELETE_LOG = "[FCM][토큰 삭제] ";
    private static final AndroidConfig ANDROID_CONFIG = AndroidConfig.builder()
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(AndroidNotification.builder()
                    .setClickAction("PUSH_CLICK")
                    .build())
            .build();
    private static final ApnsConfig APNS_CONFIG = ApnsConfig.builder()
            .setAps(Aps.builder()
                    .setCategory("PUSH_CLICK")
                    .build())
            .build();

    private final FirebaseMessaging firebaseMessaging;
    private final NotificationTokenRepository notificationTokenRepository;
    private final Executor fcmCallbackExecutor;

    public void sendPush(List<String> tokens, PushMessage pushMessage) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn(SEND_FAIL_LOG + "FCM 전송 대상 토큰이 없습니다.");
            return;
        }
        for (int i = 0; i < tokens.size(); i += FCM_MULTICAST_LIMIT) {
            List<String> batchTokens = tokens.subList(i, Math.min(i + FCM_MULTICAST_LIMIT, tokens.size()));
            MulticastMessage message = createMulticastMessage(batchTokens, pushMessage);
            ApiFuture<BatchResponse> future = firebaseMessaging.sendEachForMulticastAsync(message);
            registerCallback(future, batchTokens);
        }
    }

    private MulticastMessage createMulticastMessage(List<String> tokens, PushMessage pushMessage) {
        return MulticastMessage.builder()
                .setAndroidConfig(ANDROID_CONFIG)
                .setApnsConfig(APNS_CONFIG)
                .addAllTokens(tokens)
                .setNotification(pushMessage.toNotification())
                .putAllData(Objects.requireNonNullElse(pushMessage.toData(), Map.of()))
                .build();
    }

    private void registerCallback(ApiFuture<BatchResponse> future, List<String> tokens) {
        ApiFutures.addCallback(future, new ApiFutureCallback<>() {
            @Override
            public void onSuccess(BatchResponse response) {
                handleBatchResponse(response, tokens);
            }

            @Override
            public void onFailure(Throwable t) {
                log.error(SEND_FAIL_LOG + "FCM 전송 중 예외 발생", t);
            }
        }, fcmCallbackExecutor);
    }

    private void handleBatchResponse(BatchResponse response, List<String> tokens) {
        log.info(SEND_SUCCESS_LOG + "성공: {}, 실패: {}", response.getSuccessCount(), response.getFailureCount());
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            SendResponse sendResponse = responses.get(i);
            if (!sendResponse.isSuccessful()) {
                handleFailure(sendResponse, tokens.get(i));
            }
        }
    }

    private void handleFailure(SendResponse sendResponse, String token) {
        MessagingErrorCode errorCode = sendResponse.getException().getMessagingErrorCode();
        log.warn(SEND_FAIL_LOG + "token: {}, error: {}", token, errorCode);
        if (MessagingErrorCode.UNREGISTERED == errorCode) {
            log.warn(TOKEN_DELETE_LOG + "삭제 대상 토큰: {}", token);
            notificationTokenRepository.deleteAllByToken(token);
        }
    }
}
