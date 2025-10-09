package com.staccato.image.infrastructure;

import java.util.ArrayList;
import java.util.List;
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
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@Profile({"dev", "prod", "test"})
public class AwsS3Service implements CloudStorageService {
    private static final Logger log = LoggerFactory.getLogger(AwsS3Service.class);
    private static final int S3_DELETE_BATCH_LIMIT = 1000;

    private final S3Client s3Client;
    private final String bucketName;
    private final String endPoint;
    private final String cloudFrontEndPoint;

    public AwsS3Service(
            S3Client s3Client,
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

    @Override
    public void putS3Object(String objectKey, String contentType, byte[] imageBytes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
    }

    @Override
    public String getUrl(String keyName) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        String url = s3Client.utilities().getUrl(request).toString();

        return url.replace(endPoint, cloudFrontEndPoint);
    }

    @Override
    public String extractKeyFromUrl(String url) {
        return url.replace(cloudFrontEndPoint + "/", "");
    }

    @Override
    public DeletionResult deleteUnusedObjects(Set<String> usedKeys) {
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failCounter = new AtomicInteger(0);

        String continuationToken = null;
        List<String> toDeleteBatch = new ArrayList<>();

        do {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .continuationToken(continuationToken)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            for (S3Object object : response.contents()) {
                String key = object.key();
                if (usedKeys.contains(key))
                    continue;

                toDeleteBatch.add(key);
                if (toDeleteBatch.size() == S3_DELETE_BATCH_LIMIT) {
                    deleteObjectsBatch(toDeleteBatch, successCounter, failCounter);
                    toDeleteBatch.clear();
                }
            }

            continuationToken = response.nextContinuationToken();
        } while (continuationToken != null);

        if (!toDeleteBatch.isEmpty()) {
            deleteObjectsBatch(toDeleteBatch, successCounter, failCounter);
        }

        return new DeletionResult(successCounter.get(), failCounter.get());
    }

    private void deleteObjectsBatch(List<String> keys, AtomicInteger successCounter, AtomicInteger failCounter) {
        if (keys.isEmpty())
            return;

        List<ObjectIdentifier> objects = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();
        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objects).build())
                .build();

        try {
            DeleteObjectsResponse result = s3Client.deleteObjects(deleteRequest);
            successCounter.addAndGet(result.deleted().size());
            failCounter.addAndGet(result.errors().size());
        } catch (S3Exception | SdkClientException e) {
            failCounter.addAndGet(keys.size());
            log.warn("일괄 삭제 실패: {}", e.getMessage());
        }
    }
}
