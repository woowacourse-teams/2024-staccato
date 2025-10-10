package com.staccato.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.image.service.dto.DeletionResult;
import com.sun.java.accessibility.util.EventID;

class FakeS3ServiceTest {

    private FakeS3Service fakeS3Service;

    @BeforeEach
    void setup() {
        S3UrlResolver resolver = new S3UrlResolver("dummy-bucket", "", "https://cdn.example.com");
        fakeS3Service = new FakeS3Service(resolver);
    }

    @Test
    @DisplayName("putS3Object -> getUrl -> extractKeyFromUrl 흐름이 정상 동작한다")
    void putAndGetAndExtract() {
        // given
        String key = "images/hello.png";

        // when
        fakeS3Service.putS3Object(key, "image/png", new byte[]{1,2,3});
        String url = fakeS3Service.getUrl(key);
        String extracted = fakeS3Service.extractKeyFromUrl(url);

        // then
        assertAll(
                () -> assertThat(url).isEqualTo("https://cdn.example.com/images/hello.png"),
                () -> assertThat(extracted).isEqualTo(key)
        );
    }

    @Test
    @DisplayName("deleteUnusedObjects는 usedKeys에 없는 객체만 삭제한다")
    void deleteUnusedObjects() {
        // given
        fakeS3Service.putS3Object("images/k1.jpg", "image/jpeg", new byte[]{});
        fakeS3Service.putS3Object("images/k2.jpg", "image/jpeg", new byte[]{});
        fakeS3Service.putS3Object("images/k3.jpg", "image/jpeg", new byte[]{});

        // when
        DeletionResult result = fakeS3Service.deleteUnusedObjects(Set.of("images/k1.jpg"));

        // then
        assertAll(
                () -> assertThat(result.successCount()).isEqualTo(2),
                () -> assertThat(result.failedCount()).isZero()
        );
    }
}
