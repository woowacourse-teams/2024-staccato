package com.staccato.image.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.staccato.image.service.dto.DeletionResult;

@Service
@Profile("local")
public class FakeS3Client implements CloudStorageClient {

    private final Set<String> storedKeys = ConcurrentHashMap.newKeySet();
    private final S3UrlResolver s3UrlResolver;

    public FakeS3Client(S3UrlResolver s3UrlResolver) {
        this.s3UrlResolver = s3UrlResolver;
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
