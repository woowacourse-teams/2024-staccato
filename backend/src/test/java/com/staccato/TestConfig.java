package com.staccato;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.staccato.image.domain.ImageClient;

@TestConfiguration
public class TestConfig {
    @Bean
    public ImageClient cloudStorageClient() {
        return new FakeImageClient();
    }
}
