package com.staccato.image.infrastructure;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FakeS3ObjectClient extends S3ObjectClient {
    public static final String FAKE_CLOUD_FRONT_END_POINT = "fakeCloudFrontEndPoint";
    private final Set<String> storedKeys = new HashSet<>();

    public FakeS3ObjectClient() {
        super("fakeBuket", "fakeEndPoint", FAKE_CLOUD_FRONT_END_POINT, "fakeAccessKey", "fakeSecretAccessKey");
    }

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
    public int deleteUnusedObjects(Set<String> usedKeys) {
        Set<String> toDelete = storedKeys.stream()
                .filter(stored -> !usedKeys.contains(stored))
                .collect(Collectors.toSet());
        storedKeys.removeAll(toDelete);
        return toDelete.size();
    }
}
