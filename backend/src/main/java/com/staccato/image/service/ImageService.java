package com.staccato.image.service;

import java.io.IOException;
import java.util.List;
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
import com.staccato.image.service.dto.DeletionResult;
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
    private final CloudStorageService cloudStorageService;
    private final StaccatoImageRepository staccatoImageRepository;
    private final CategoryRepository categoryRepository;

    public ImageUrlResponse uploadImage(MultipartFile image) {
        String imageExtension = getImageExtension(image);
        String key = imageFolderName + UUID.randomUUID() + imageExtension;
        String contentType = ImageExtension.getContentType(imageExtension);
        byte[] imageBytes = getImageBytes(image);

        try {
            cloudStorageService.putS3Object(key, contentType, imageBytes);
            String imageUrl = cloudStorageService.getUrl(key);
            log.info("Image uploaded successfully: {}", imageUrl);
            return new ImageUrlResponse(imageUrl);
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            throw new StaccatoException("이미지 업로드에 실패했습니다.");
        }
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

    @Transactional
    public DeletionResult deleteUnusedImages() {
        Set<String> allImageUrls = extractAllImageUrls();
        try {
            return cloudStorageService.deleteUnusedObjects(allImageUrls);
        } catch (Exception e) {
            throw new RuntimeException("사용하지 않는 이미지 삭제 중 오류 발생", e);
        }
    }

    private Set<String> extractAllImageUrls() {
        try (Stream<String> categoryUrls = categoryRepository.findAllThumbnailUrls();
             Stream<String> staccatoUrls = staccatoImageRepository.findAllImageUrls()) {
            List<String> allUrls = Stream.concat(categoryUrls, staccatoUrls).toList();
            return allUrls.stream()
                    .map(cloudStorageService::extractKeyFromUrl)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("사용 이미지 키 추출 중 오류 발생", e);
        }
    }
}
