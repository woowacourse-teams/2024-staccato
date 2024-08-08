package com.staccato;

import com.staccato.s3.domain.CloudStorageClient;

public class FakeCloudStorageClient extends CloudStorageClient {
    public FakeCloudStorageClient() {
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
