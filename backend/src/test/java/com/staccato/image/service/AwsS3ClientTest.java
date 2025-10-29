package com.staccato.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import com.staccato.image.infrastructure.S3Config;
import com.staccato.image.service.dto.DeletionResult;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Disabled("현재 LocalStack 테스트 비활성화 중")
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AwsS3ClientTest {

    private static final DockerImageName LOCALSTACK_IMAGE =
            DockerImageName.parse("localstack/localstack:4.6.0");

    private LocalStackContainer localStack;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
    }

    @Configuration
    @Import({S3Config.class, AwsS3Client.class})
    static class TestConfig {
    }

    @Autowired
    private S3Client s3Client;
    @Autowired
    private AwsS3Client awsS3Client;

    @Value("${cloud.aws.s3.bucket:staccato-test-bucket}")
    private String bucket;
    @Value("${cloud.aws.cloudfront.endpoint:http://cdn.localstack.test}")
    private String cloudFrontEndpoint;

    @BeforeAll
    void setupLocalStack() {
        localStack = new LocalStackContainer(LOCALSTACK_IMAGE)
                .withServices(LocalStackContainer.Service.S3);
        localStack.start();

        System.setProperty("cloud.aws.s3.endpoint",
                localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        System.setProperty("cloud.aws.region", localStack.getRegion());
        System.setProperty("cloud.aws.access-key", localStack.getAccessKey());
        System.setProperty("cloud.aws.secret-access-key", localStack.getSecretKey());
        System.setProperty("cloud.aws.s3.bucket", "staccato-test-bucket");
        System.setProperty("cloud.aws.cloudfront.endpoint", "http://cdn.localstack.test");

        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException ignored) {
        }
    }

    @BeforeEach
    void clearBucket() {
        s3Client.listObjectsV2(b -> b.bucket(bucket))
                .contents()
                .forEach(o -> s3Client.deleteObject(d -> d.bucket(bucket).key(o.key())));
    }

    @Test
    @DisplayName("putS3Object -> getUrl -> extractKeyFromUrl 흐름이 LocalStack에서 정상 동작한다")
    void putAndGetAndExtract() {
        String key = "image/test1.png";
        byte[] body = new byte[]{1, 2, 3};

        awsS3Client.putS3Object(key, "image/png", body);
        String url = awsS3Client.getUrl(key);
        String extracted = awsS3Client.extractKeyFromUrl(url);

        assertAll(
                () -> assertThat(url).startsWith(cloudFrontEndpoint + "/"),
                () -> assertThat(url).endsWith("/" + key),
                () -> assertThat(extracted).isEqualTo(key)
        );
    }

    @Test
    @DisplayName("deleteUnusedObjects는 usedKeys에 없는 객체만 삭제한다")
    void deleteUnusedObjects() {
        put("k1");
        put("k2");
        put("k3");

        DeletionResult result = awsS3Client.deleteUnusedObjects(Set.of("k1"));

        assertAll(
                () -> assertThat(result.successCount()).isEqualTo(2),
                () -> assertThat(result.failedCount()).isZero()
        );
    }

    private void put(String key) {
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(key).contentType("image/png").build(),
                RequestBody.fromBytes(new byte[]{1}));
    }
}