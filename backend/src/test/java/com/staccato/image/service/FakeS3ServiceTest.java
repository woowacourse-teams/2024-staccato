package com.staccato.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.image.service.dto.DeletionResult;

class FakeS3ServiceTest {

    @Test
    @DisplayName("putS3Object -> getUrl -> extractKeyFromUrl 흐름이 정상 동작한다")
    void putAndGetAndExtract() {
        // given
        FakeS3Service fakeS3Service = new FakeS3Service(
                "dummy-bucket", "", "https://cdn.example.com");
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
        FakeS3Service svc = new FakeS3Service("dummy-bucket", "", "https://cdn.example.com");
        svc.putS3Object("images/k1.jpg", "image/jpeg", new byte[]{});
        svc.putS3Object("images/k2.jpg", "image/jpeg", new byte[]{});
        svc.putS3Object("images/k3.jpg", "image/jpeg", new byte[]{});

        // when
        DeletionResult result = svc.deleteUnusedObjects(Set.of("images/k1.jpg"));

        // then
        assertAll(
                () -> assertThat(result.successCount()).isEqualTo(2),
                () -> assertThat(result.failedCount()).isZero()
        );
    }
}
