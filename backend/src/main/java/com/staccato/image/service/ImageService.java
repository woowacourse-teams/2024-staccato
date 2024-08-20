package com.staccato.image.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.StaccatoException;
import com.staccato.image.domain.ImageExtension;
import com.staccato.image.infrastructure.S3ObjectClient;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.image.service.dto.ImageUrlResponses;
import com.staccato.image.service.dto.ImageUrlWithStatusResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
    private static final String TEAM_FOLDER = "staccato/";
    private static final String IMAGE_FOLDER = "image/";

    private final S3ObjectClient s3ObjectClient;

    public ImageUrlResponses uploadImages(List<MultipartFile> images) {
        return new ImageUrlResponses(images.stream()
                .map(this::uploadFile)
                .map(url -> new ImageUrlWithStatusResponse("200 OK", url))
                .toList());
    }

    public ImageUrlResponse uploadImage(MultipartFile image) {
        String imageExtension = getImageExtension(image);
        String key = TEAM_FOLDER + IMAGE_FOLDER + UUID.randomUUID() + imageExtension;
        String contentType = ImageExtension.getContentType(imageExtension);
        byte[] imageBytes = getImageBytes(image);

        s3ObjectClient.putS3Object(key, contentType, imageBytes);
        String imageUrl = s3ObjectClient.getUrl(key);

        return new ImageUrlResponse(imageUrl);
    }

    public String uploadFile(MultipartFile file) {
        String fileExtension = getImageExtension(file);
        String key = UUID.randomUUID() + fileExtension;
        String contentType = ImageExtension.getContentType(fileExtension);
        byte[] fileBytes = getImageBytes(file);

        s3ObjectClient.putS3Object(key, contentType, fileBytes);

        return s3ObjectClient.getUrl(key);
    }

    private byte[] getImageBytes(MultipartFile image) {
        try {
            return image.getBytes();
        } catch (IOException e) {
            throw new StaccatoException("전송된 파일이 손상되었거나 지원되지 않는 형식입니다.");
        }
    }

    private String getImageExtension(MultipartFile image) {
        String imageName = image.getOriginalFilename();
        if (imageName == null || !imageName.contains(".")) {
            return "";
        }
        return imageName.substring(imageName.lastIndexOf('.'));
    }
}
