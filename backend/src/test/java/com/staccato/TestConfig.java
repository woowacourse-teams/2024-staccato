package com.staccato;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.staccato.s3.domain.CloudStorageClient;

@TestConfiguration
public class TestConfig {
    @Bean
    public CloudStorageClient cloudStorageClient() {
        return new FakeCloudStorageClient();
    }
}
