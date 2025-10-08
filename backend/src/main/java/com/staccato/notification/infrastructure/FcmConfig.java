package com.staccato.notification.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

    @Bean
    @Profile("prod")
    public FirebaseMessaging firebaseMessaging(@Value("${fcm.admin-sdk}") String adminSdk) {
        initializeFirebase(adminSdk);
        return FirebaseMessaging.getInstance();
    }

    private void initializeFirebase(String adminSdk) {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info(SUCCESS_LOG + "기존 FirebaseApp 인스턴스가 존재하므로 초기화 생략");
            return;
        }

        try {
            FirebaseApp.initializeApp(buildFirebaseOptions(adminSdk));
            log.info(SUCCESS_LOG + "설정 성공");
        } catch (IOException e) {
            log.error(FAIL_LOG + "연결 오류 {}", e.getMessage());
            throw new IllegalStateException("FCM 초기화 실패", e);
        }
    }

    private FirebaseOptions buildFirebaseOptions(String adminSdk) throws IOException {
        return FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(adminSdk.getBytes())))
                .build();
    }
}
