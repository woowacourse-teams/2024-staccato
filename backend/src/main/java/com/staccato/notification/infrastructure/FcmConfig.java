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
    @Profile({"prod", "dev"})
    public FirebaseMessaging firebaseMessaging(@Value("${fcm.admin-sdk}") String adminSdk) {
        initializeFirebase(adminSdk);
        return FirebaseMessaging.getInstance();
    }

    private void initializeFirebase(String adminSdk) {
        if (isFirebaseInitialized()) {
            log.info(SUCCESS_LOG + "기존 FirebaseApp 인스턴스가 존재하므로 초기화 생략");
            return;
        }

        log.info("[FCM] 기본 FirebaseApp이 초기화되지 않았습니다. 초기화를 시작합니다.");
        initializeFirebaseApp(adminSdk);
    }

    private boolean isFirebaseInitialized() {
        try {
            FirebaseApp.getInstance();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private void initializeFirebaseApp(String adminSdk) {
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
