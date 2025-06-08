package com.staccato.notification.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FcmConfig {

    @Value("${fcm.admin-sdk}")
    private String adminSdk;

    @PostConstruct
    public void initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(adminSdk.getBytes())))
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("FCM 설정 성공");
            } catch (IOException exception) {
                log.error("FCM 연결 오류 {}", exception.getMessage());
            }
        } else {
            log.info("기존 FirebaseApp 인스턴스가 존재하므로 초기화 생략");
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
