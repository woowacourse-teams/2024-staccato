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
    private final String endPoint;
    private final String cloudFrontEndPoint;

    public FakeS3Service(
            @Value("${cloud.aws.s3.endpoint:}") String endPoint,
            @Value("${cloud.aws.cloudfront.endpoint}") String cloudFrontEndPoint
    ) {
        this.endPoint = endPoint;
        this.cloudFrontEndPoint = cloudFrontEndPoint;
    }

    @Override
    public void putS3Object(String objectKey, String contentType, byte[] imageBytes) {
        storedKeys.add(objectKey);
    }

    @Override
    public String getUrl(String keyName) {
        String base = "http://fake-s3";
        if (hasText(cloudFrontEndPoint)) {
            base = cloudFrontEndPoint;
        } else if (hasText(endPoint)) {
            base = endPoint;
        }

        if (base.endsWith("/")) {
            return base + keyName;
        }
        return base + "/" + keyName;
    }

    @Override
    public String extractKeyFromUrl(String url) {
        if (hasText(cloudFrontEndPoint)) {
            return url.replace(cloudFrontEndPoint + "/", "");
        }
        if (hasText(endPoint)) {
            return url.replace(endPoint + "/", "");
        }
        return url;
    }

    @Override
    public DeletionResult deleteUnusedObjects(Set<String> usedKeys) {
        int before = storedKeys.size();
        storedKeys.removeIf(key -> !usedKeys.contains(key));
        int deleted = before - storedKeys.size();
        return new DeletionResult(deleted, 0);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
