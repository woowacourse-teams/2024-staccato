package com.staccato;

import com.staccato.image.domain.S3Client;

public class FakeS3Client extends S3Client {
    public FakeS3Client() {
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
