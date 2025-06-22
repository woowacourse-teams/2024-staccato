package com.staccato.notification.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;

@ExtendWith(MockitoExtension.class)
class FcmServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;
    @Mock
    private ApiFuture<BatchResponse> future;
    @InjectMocks
    private FcmService fcmService;

    @DisplayName("정상적인 메시지를 전송하면 FCM을 호출한다.")
    @Test
    void sendPush() {
        // given
        List<String> tokens = List.of("token1", "token2");
        when(firebaseMessaging.sendEachForMulticastAsync(any(MulticastMessage.class))).thenReturn(future);

        // when
        fcmService.sendPush(tokens, Map.of());

        // then
        verify(firebaseMessaging).sendEachForMulticastAsync(any(MulticastMessage.class));
    }

    @DisplayName("토큰 리스트가 비어있으면 FCM을 호출하지 않는다.")
    @Test
    void failSendPush() {
        // given
        fcmService.sendPush(List.of(), Map.of());

        // when & then
        verifyNoInteractions(firebaseMessaging);
    }
}
