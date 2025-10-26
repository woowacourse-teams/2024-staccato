package com.staccato.image.infrastructure;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.staccato.image.service.dto.DeletionResult;

public class FakeS3ObjectClient implements CloudStorageClient {
    public static final String FAKE_CLOUD_FRONT_END_POINT = "fakeCloudFrontEndPoint";
    private final Set<String> storedKeys = new HashSet<>();

    @Override
    public void putS3Object(String objectKey, String contentType, byte[] imageBytes) {
        storedKeys.add(objectKey);
    }

    @Override
    public String getUrl(String keyName) {
        return FAKE_CLOUD_FRONT_END_POINT + "/" + keyName;
    }

    @Override
    public String extractKeyFromUrl(String url) {
        return url.replace(FAKE_CLOUD_FRONT_END_POINT + "/", "");
    }

    @Override
    public DeletionResult deleteUnusedObjects(Set<String> usedKeys) {
        Set<String> toDelete = storedKeys.stream()
                .filter(stored -> !usedKeys.contains(stored))
                .collect(Collectors.toSet());
        storedKeys.removeAll(toDelete);
        return new DeletionResult(toDelete.size(), 0);
    }
}
