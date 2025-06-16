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

    private static final String SUCCESS_LOG = "[FCM][연결 성공] ";
    private static final String FAIL_LOG = "[FCM][연결 실패] ";

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
                log.info(SUCCESS_LOG + "설정 성공");
            } catch (IOException exception) {
                log.error(FAIL_LOG + "연결 오류 {}", exception.getMessage());
            }
        } else {
            log.info(SUCCESS_LOG + "기존 FirebaseApp 인스턴스가 존재하므로 초기화 생략");
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
