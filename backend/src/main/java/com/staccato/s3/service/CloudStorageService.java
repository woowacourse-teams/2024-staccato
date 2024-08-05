package com.staccato.s3.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.s3.domain.CloudStorageClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudStorageService {
    private static final String IMAGE_FOLDER = "image/";

    private final CloudStorageClient cloudStorageClient;

    public String uploadFile(MultipartFile image) throws IOException {
        String key = makeImagePath(image.getOriginalFilename());
        cloudStorageClient.putS3Object(key, image.getContentType(), image.getBytes());

        return cloudStorageClient.getUrl(key);
    }

    private String makeImagePath(String fileName) {
        return IMAGE_FOLDER + fileName;
    }
}
