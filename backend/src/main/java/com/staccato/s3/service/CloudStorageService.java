package com.staccato.s3.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.s3.domain.CloudStorageClient;
import com.staccato.s3.domain.FileExtension;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudStorageService {
    private static final String TEAM_FOLDER = "staccato/";
    private static final String IMAGE_FOLDER = "image/";

    private final CloudStorageClient cloudStorageClient;

    public List<String> uploadFiles(List<MultipartFile> images) {
        return images.stream()
                .map(this::uploadFile)
                .toList();
    }

    public String uploadFile(MultipartFile image) {
        String fileName = image.getOriginalFilename();
        String key = makeImagePath(fileName);
        String contentType = getContentType(fileName);
        try {
            cloudStorageClient.putS3Object(key, contentType, image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cloudStorageClient.getUrl(key);
    }

    private String makeImagePath(String fileName) {
        String uniqueFileName = generateUniqueFileName(fileName);
        return TEAM_FOLDER + IMAGE_FOLDER + uniqueFileName;
    }

    private String generateUniqueFileName(String fileName) {
        String fileExtension = getFileExtension(fileName);
        return UUID.randomUUID() + fileExtension;
    }

    private String getContentType(String fileName) {
        String fileExtension = getFileExtension(fileName);

        return FileExtension.getContentType(fileExtension);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex);
    }

    public void deleteFiles(List<String> imageUrls) {
        List<String> objectKeys = imageUrls.stream()
                .map(this::getObjectKeyFromUrl)
                .toList();

        if (imageUrls.size() == 1) {
            cloudStorageClient.deleteS3Object(objectKeys.get(0));
            return;
        }

        cloudStorageClient.deleteMultipleS3Object(objectKeys);
    }

    private String getObjectKeyFromUrl(String imageUrl) {
        URI uri = createUri(imageUrl);
        String path = uri.getPath();

        if (path != null && path.startsWith("/")) {
            return path.substring(1);
        }

        throw new IllegalArgumentException("S3 URL 형식이 올바르지 않습니다.");
    }

    private URI createUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL 형식이 올바르지 않습니다.");
        }
    }
}
