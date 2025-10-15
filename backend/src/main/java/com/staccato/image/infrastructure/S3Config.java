package com.staccato.image.infrastructure;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.staccato.image.service.S3UrlResolver;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3Config {

    @Bean
    @ConditionalOnProperty(name = "cloud.storage.mode", havingValue = "aws", matchIfMissing = true)
    public S3Client awsClient(
            @Value("${cloud.aws.access-key}") String accessKey,
            @Value("${cloud.aws.secret-access-key}") String secretKey,
            @Value("${cloud.aws.region}") String region
    ) {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "cloud.storage.mode", havingValue = "localstack")
    public S3Client localstackClient(
            @Value("${cloud.aws.s3.endpoint}") String endpoint,
            @Value("${cloud.aws.region}") String region,
            @Value("${cloud.aws.access-key}") String accessKey,
            @Value("${cloud.aws.secret-access-key}") String secretKey
    ) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    public S3UrlResolver s3UrlResolver(
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.s3.endpoint}") String endpoint,
            @Value("${cloud.aws.cloudfront.endpoint}") String cloudFrontEndPoint
    ) {
        return new S3UrlResolver(bucket, endpoint, cloudFrontEndPoint);
    }
}
