package com.staccato.image.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.category.repository.CategoryRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.StaccatoException;
import com.staccato.image.domain.ImageExtension;
import com.staccato.image.infrastructure.S3ObjectClient;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.staccato.repository.StaccatoImageRepository;

import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    @Value("${image.folder.name}")
    private String imageFolderName;
    private final S3ObjectClient s3ObjectClient;
    private final StaccatoImageRepository staccatoImageRepository;
    private final CategoryRepository categoryRepository;

    public ImageUrlResponse uploadImage(MultipartFile image) {
        String imageExtension = getImageExtension(image);
        String key = imageFolderName + UUID.randomUUID() + imageExtension;
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

    @Transactional(readOnly = true)
    public int deleteUnusedImages() {
        Set<String> usedKeys = new HashSet<>();

        try (Stream<String> categoryUrls = categoryRepository.findAllThumbnailUrls();
             Stream<String> staccatoUrls = staccatoImageRepository.findAllImageUrls()) {
            usedKeys.addAll(extractUsedKeys(categoryUrls));
            usedKeys.addAll(extractUsedKeys(staccatoUrls));
        } catch (Exception e) {
            throw new RuntimeException("사용 이미지 키 추출 중 오류 발생", e);
        }

        return s3ObjectClient.deleteUnusedObjects(usedKeys);
    }

    private Set<String> extractUsedKeys(Stream<String> urlStream) {
        return urlStream
                .filter(Objects::nonNull)
                .map(s3ObjectClient::extractKeyFromUrl)
                .collect(Collectors.toSet());
    }
}
