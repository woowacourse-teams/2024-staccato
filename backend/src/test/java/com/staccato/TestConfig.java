package com.staccato;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.staccato.infrastructure.FakeS3ObjectClient;
import com.staccato.infrastructure.S3ObjectClient;

@TestConfiguration
public class TestConfig {
    @Bean
    public S3ObjectClient cloudStorageClient() {
        return new FakeS3ObjectClient();
    }
}
