package com.staccato;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.google.firebase.messaging.FirebaseMessaging;

@TestConfiguration
public class FcmTestConfig {

    @MockBean
    public FirebaseMessaging firebaseMessaging;

}
