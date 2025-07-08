package com.staccato.image.infrastructure;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.staccato.image.service.dto.DeletionResult;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
public class S3ObjectClient {
    private static final Logger log = LoggerFactory.getLogger(S3ObjectClient.class);
    private final S3Client s3Client;
    private final String bucketName;
    private final String endPoint;
    private final String cloudFrontEndPoint;

    public S3ObjectClient(
            @Value("${cloud.aws.s3.bucket}") String bucketName,
            @Value("${cloud.aws.s3.endpoint}") String endPoint,
            @Value("${cloud.aws.cloudfront.endpoint}") String cloudFrontEndPoint,
            @Value("${cloud.aws.access-key}") String accessKey,
            @Value("${cloud.aws.secret-access-key}") String secretAccessKey
    ) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.AP_NORTHEAST_2)
                .build();
        this.bucketName = bucketName;
        this.endPoint = endPoint;
        this.cloudFrontEndPoint = cloudFrontEndPoint;
    }

    public void putS3Object(String objectKey, String contentType, byte[] imageBytes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
    }

    public String getUrl(String keyName) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        String url = s3Client.utilities().getUrl(request).toString();

        return url.replace(endPoint, cloudFrontEndPoint);
    }

    public String extractKeyFromUrl(String url) {
        return url.replace(cloudFrontEndPoint + "/", "");
    }

    public DeletionResult deleteUnusedObjects(Set<String> usedKeys) {
        String continuationToken = null;
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failCounter = new AtomicInteger(0);
        do {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .continuationToken(continuationToken)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            for (S3Object object : response.contents()) {
                String key = object.key();
                if (!usedKeys.contains(key)) {
                    deleteOneObject(key, successCounter, failCounter);
                }
            }
            continuationToken = response.nextContinuationToken();
        } while (continuationToken != null);
        return new DeletionResult(successCounter.get(), failCounter.get());
    }

    private void deleteOneObject(String key, AtomicInteger successCounter, AtomicInteger failCounter) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            successCounter.incrementAndGet();
        } catch (S3Exception | SdkClientException e) {
            failCounter.incrementAndGet();
            log.warn("삭제 실패: {} - {}", key, e.getMessage());
        }
    }
}
