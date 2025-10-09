package com.staccato.image.service;

import java.util.Set;

import com.staccato.image.service.dto.DeletionResult;

public interface CloudStorageService {
    void putS3Object(String objectKey, String contentType, byte[] imageBytes);
    String getUrl(String keyName);
    String extractKeyFromUrl(String url);
    DeletionResult deleteUnusedObjects(Set<String> usedKeys);
}
