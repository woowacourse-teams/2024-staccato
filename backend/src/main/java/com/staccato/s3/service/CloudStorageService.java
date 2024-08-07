package com.staccato.s3.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.s3.domain.CloudStorageClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudStorageService {
    private static final String TEAM_FOLDER = "staccato/";
    private static final String IMAGE_FOLDER = "image/";

    private final CloudStorageClient cloudStorageClient;

    public String uploadFile(MultipartFile image) throws IOException {
        String key = makeImagePath(image.getOriginalFilename());
        cloudStorageClient.putS3Object(key, image.getContentType(), image.getBytes());

        return cloudStorageClient.getUrl(key);
    }

    private String makeImagePath(String fileName) {
        String uniqueFileName = generateUniqueFileName(fileName);
        return TEAM_FOLDER + IMAGE_FOLDER + uniqueFileName;
    }

    private String generateUniqueFileName(String fileName) {
        String fileExtension = getFileExtension(fileName);
        String baseName = getBaseName(fileName);
        return baseName + "_" + UUID.randomUUID() + fileExtension;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex);
    }

    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }
}
