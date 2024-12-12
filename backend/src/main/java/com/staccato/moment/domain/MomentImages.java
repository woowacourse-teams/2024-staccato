package com.staccato.moment.domain;

import java.util.List;
import com.staccato.exception.StaccatoException;

public record MomentImages(List<MomentImage> images) {
    private static final int MAX_COUNT = 5;

    public MomentImages {
        validateNumberOfImages(images);
    }

    public static MomentImages of(List<String> imageUrls, Moment moment) {
        List<MomentImage> images = imageUrls.stream()
                .map(imageUrl -> new MomentImage(imageUrl, moment))
                .toList();
        return new MomentImages(images);
    }

    private <T> void validateNumberOfImages(List<T> addedImages) {
        if (addedImages.size() > MAX_COUNT) {
            throw new StaccatoException("사진은 5장을 초과할 수 없습니다.");
        }
    }

    public List<MomentImage> findValuesNotPresentIn(MomentImages targetMomentImages) {
        return this.images.stream()
                .filter(image -> !targetMomentImages.contains(image))
                .toList();
    }

    private boolean contains(MomentImage momentImage) {
        return this.images.stream()
                .anyMatch(image -> image.getImageUrl().equals(momentImage.getImageUrl()));
    }

    public List<String> getUrls() {
        return images.stream()
                .map(MomentImage::getImageUrl)
                .toList();
    }
}
