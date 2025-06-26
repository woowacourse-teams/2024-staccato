package com.staccato;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import com.google.firebase.messaging.FirebaseMessaging;
import com.staccato.image.infrastructure.FakeS3ObjectClient;
import com.staccato.image.infrastructure.S3ObjectClient;

@TestConfiguration
public class ServiceTestConfig {
    @MockBean
    private FirebaseMessaging firebaseMessaging;

    @Bean
    public S3ObjectClient cloudStorageClient() {
        return new FakeS3ObjectClient();
    }
}
