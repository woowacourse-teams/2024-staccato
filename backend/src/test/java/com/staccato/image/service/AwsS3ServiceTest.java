package com.staccato.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.staccato.image.infrastructure.S3Config;
import com.staccato.image.service.dto.DeletionResult;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AwsS3ServiceTest {

    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:2.3");
    private static final LocalStackContainer LOCAL_STACK =
            new LocalStackContainer(LOCALSTACK_IMAGE).withServices(LocalStackContainer.Service.S3);

    static {
        LOCAL_STACK.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.s3.endpoint", () -> LOCAL_STACK.getEndpointOverride(LocalStackContainer.Service.S3)
                .toString());
        registry.add("cloud.aws.region", LOCAL_STACK::getRegion);
        registry.add("cloud.aws.access-key", LOCAL_STACK::getAccessKey);
        registry.add("cloud.aws.secret-access-key", LOCAL_STACK::getSecretKey);
        registry.add("cloud.aws.s3.bucket", () -> "staccato-test-bucket");
        registry.add("cloud.aws.cloudfront.endpoint", () -> "http://cdn.localstack.test");
    }

    @Configuration
    @Import({S3Config.class, AwsS3Service.class})
    static class TestConfig {
    }

    @Autowired
    private S3Client s3Client;
    @Autowired
    private AwsS3Service awsS3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.cloudfront.endpoint}")
    private String cloudFrontEndpoint;

    @BeforeAll
    void createBucket() {
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
        // given
        String key = "image/test1.png";
        byte[] body = new byte[]{1, 2, 3};

        // when
        awsS3Service.putS3Object(key, "image/png", body);
        String url = awsS3Service.getUrl(key);
        String extracted = awsS3Service.extractKeyFromUrl(url);

        // then
        assertAll(
                () -> assertThat(url).startsWith(cloudFrontEndpoint + "/"),
                () -> assertThat(url).endsWith("/" + key),
                () -> assertThat(extracted).isEqualTo(key)
        );
    }

    @Test
    @DisplayName("deleteUnusedObjects는 usedKeys에 없는 객체만 삭제한다")
    void deleteUnusedObjects() {
        // given
        put("k1");
        put("k2");
        put("k3");

        // when
        DeletionResult result = awsS3Service.deleteUnusedObjects(Set.of("k1"));

        System.out.println(result);

        // then
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
