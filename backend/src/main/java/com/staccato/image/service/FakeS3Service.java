package com.staccato.image.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.staccato.image.service.dto.DeletionResult;

@Service
@Profile("local")
public class FakeS3Service implements CloudStorageService {

    private final Set<String> storedKeys = new HashSet<>();
    private final S3UrlResolver s3UrlResolver;

    public FakeS3Service(
            @Value("${cloud.aws.s3.bucket:dummy-bucket}") String bucket,
            @Value("${cloud.aws.s3.endpoint:}") String endPoint,
            @Value("${cloud.aws.cloudfront.endpoint:}") String cloudFrontEndPoint
    ) {
        this.s3UrlResolver = new S3UrlResolver(bucket, endPoint, cloudFrontEndPoint);
    }

    @Override
    public void putS3Object(String objectKey, String contentType, byte[] imageBytes) {
        storedKeys.add(objectKey);
    }

    @Override
    public String getUrl(String keyName) {
        return s3UrlResolver.toPublicUrl(keyName);
    }

    @Override
    public String extractKeyFromUrl(String url) {
        return s3UrlResolver.extractKey(url);
    }

    @Override
    public DeletionResult deleteUnusedObjects(Set<String> usedKeys) {
        int before = storedKeys.size();
        storedKeys.removeIf(key -> !usedKeys.contains(key));
        int deleted = before - storedKeys.size();
        return new DeletionResult(deleted, 0);
    }
}
