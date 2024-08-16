package com.staccato;

import com.staccato.image.domain.ImageClient;

public class FakeImageClient extends ImageClient {
    public FakeImageClient() {
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
