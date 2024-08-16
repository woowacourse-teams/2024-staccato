package com.staccato;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.staccato.image.domain.S3Client;

@TestConfiguration
public class TestConfig {
    @Bean
    public S3Client cloudStorageClient() {
        return new FakeS3Client();
    }
}
