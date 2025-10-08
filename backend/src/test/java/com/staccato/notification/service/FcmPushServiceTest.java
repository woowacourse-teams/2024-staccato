package com.staccato.notification.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

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
import com.staccato.category.domain.Category;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.dto.message.PushMessage;
import com.staccato.notification.service.dto.message.ReceiveInvitationMessage;

@ExtendWith(MockitoExtension.class)
public class FcmPushServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;
    @Mock
    private ApiFuture<BatchResponse> future;
    @InjectMocks
    private FcmPushService fcmPushService;

    @DisplayName("정상적인 메시지를 전송하면 FCM을 호출한다.")
    @Test
    void sendPush() {
        // given
        List<String> tokens = List.of("token1", "token2");
        when(firebaseMessaging.sendEachForMulticastAsync(any(MulticastMessage.class))).thenReturn(future);

        // when
        fcmPushService.sendPush(tokens, dummyPushMessage());

        // then
        verify(firebaseMessaging).sendEachForMulticastAsync(any(MulticastMessage.class));
    }

    @DisplayName("토큰 리스트가 비어있으면 FCM을 호출하지 않는다.")
    @Test
    void failSendPush() {
        // given
        fcmPushService.sendPush(List.of(), dummyPushMessage());

        // when & then
        verifyNoInteractions(firebaseMessaging);
    }

    private PushMessage dummyPushMessage() {
        Member inviter = MemberFixtures.ofDefault().build();
        Category category = CategoryFixtures.ofDefault().build();

        return new ReceiveInvitationMessage(inviter, category);
    }
}
