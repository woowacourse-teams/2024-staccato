package com.staccato.notification.service;

import java.util.List;
import java.util.concurrent.Executor;
import org.springframework.stereotype.Service;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.staccato.config.log.annotation.Trace;
import com.staccato.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Trace
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final NotificationTokenRepository notificationTokenRepository;
    private final Executor fcmCallbackExecutor;

    public void sendPush(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) {
            log.info("FCM 전송 대상 토큰이 없습니다.");
            return;
        }

        MulticastMessage message = createMulticastMessage(tokens, title, body);
        ApiFuture<BatchResponse> future = firebaseMessaging.sendEachForMulticastAsync(message);
        registerCallback(future, tokens);
    }

    private MulticastMessage createMulticastMessage(List<String> tokens, String title, String body) {
        return MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("click_action", "push_click")
                .addAllTokens(tokens)
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
                log.error("FCM 전송 중 예외 발생", t);
            }
        }, fcmCallbackExecutor);
    }

    private void handleBatchResponse(BatchResponse response, List<String> tokens) {
        log.info("[FCM] 전송 완료 - 성공: {}, 실패: {}", response.getSuccessCount(), response.getFailureCount());
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
        log.warn("[FCM] 실패 - token: {}, error: {}", token, errorCode);
        if (MessagingErrorCode.UNREGISTERED == errorCode) {
            log.warn("[FCM] 삭제 대상 토큰: {}", token);
            notificationTokenRepository.deleteAllByToken(token);
        }
    }
}
