package com.staccato.image.infrastructure;

import com.staccato.image.infrastructure.S3ObjectClient;

public class FakeS3ObjectClient extends S3ObjectClient {
    public FakeS3ObjectClient() {
        super("fakeBuket", "fakeEndPoint", "fakeCloudFrontEndPoint");
    }

    @Override
    public void putS3Object(String objectKey, String contentType, byte[] imageBytes) {
    }

    @Override
    public String getUrl(String keyName) {
        return "fakeUrl";
    }
}
