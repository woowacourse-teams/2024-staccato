package com.staccato.image.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
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
        try (Stream<String> categoryUrls = categoryRepository.findAllThumbnailUrls()) {
            List<String> usedCategoryUrls = categoryUrls.filter(Objects::nonNull)
                    .map(s3ObjectClient::extractKeyFromUrl)
                    .toList();
            usedKeys.addAll(usedCategoryUrls);
        }
        try (Stream<String> staccatoUrls = staccatoImageRepository.findAllImageUrls()) {
            List<String> usedStaccatoUrls = staccatoUrls.filter(Objects::nonNull)
                    .map(s3ObjectClient::extractKeyFromUrl)
                    .toList();
            usedKeys.addAll(usedStaccatoUrls);
        }
        return s3ObjectClient.deleteUnusedObjects(usedKeys);
    }
}
