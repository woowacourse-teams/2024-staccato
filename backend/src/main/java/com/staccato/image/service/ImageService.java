package com.staccato.image.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.StaccatoException;
import com.staccato.image.domain.ImageExtension;
import com.staccato.image.infrastructure.S3ObjectClient;
import com.staccato.image.service.dto.ImageUrlResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
    private static final String TEAM_FOLDER_NAME = "staccato/";
    private final S3ObjectClient s3ObjectClient;
    @Value("${image.folder.name}")
    private String imageFolderName;

    public ImageUrlResponse uploadImage(MultipartFile image) {
        String imageExtension = getImageExtension(image);
        String key = TEAM_FOLDER_NAME + imageFolderName + UUID.randomUUID() + imageExtension;
        String contentType = ImageExtension.getContentType(imageExtension);
        byte[] imageBytes = getImageBytes(image);

        s3ObjectClient.putS3Object(key, contentType, imageBytes);
        String imageUrl = s3ObjectClient.getUrl(key);

        return new ImageUrlResponse(imageUrl);
    }

    private String getImageExtension(MultipartFile image) {
        String imageName = image.getOriginalFilename();
        if (imageName == null || !imageName.contains(".")) {
            return "";
        }
        return imageName.substring(imageName.lastIndexOf('.'));
    }

    private byte[] getImageBytes(MultipartFile image) {
        try {
            return image.getBytes();
        } catch (IOException e) {
            throw new StaccatoException("전송된 파일이 손상되었거나 지원되지 않는 형식입니다.");
        }
    }
}
