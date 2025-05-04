package com.staccato;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import com.staccato.image.infrastructure.FakeS3ObjectClient;
import com.staccato.image.infrastructure.S3ObjectClient;

@TestConfiguration
public class ServiceTestConfig {
    @Primary
    @Bean
    public S3ObjectClient cloudStorageClient() {
        return new FakeS3ObjectClient();
    }
}
