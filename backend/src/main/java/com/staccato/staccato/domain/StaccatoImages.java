package com.staccato.staccato.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import com.staccato.exception.StaccatoException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StaccatoImages {
    private static final int MAX_COUNT = 8;

    @OneToMany(mappedBy = "staccato", cascade = CascadeType.PERSIST)
    private List<StaccatoImage> images = new ArrayList<>();

    public StaccatoImages(List<String> addedImages) {
        validateNumberOfImages(addedImages);
        this.images.addAll(addedImages.stream()
                .map(StaccatoImage::new)
                .toList());
    }

    private void validateNumberOfImages(List<String> addedImages) {
        if (addedImages.size() > MAX_COUNT) {
            throw new StaccatoException("사진은 8장을 초과할 수 없습니다.");
        }
    }

    public boolean isNotEmpty() {
        return !images.isEmpty();
    }

    protected void update(StaccatoImages newStaccatoImages, Staccato staccato) {
        if (isImagesChanged(newStaccatoImages)) {
            staccato.updateCategoryModifiedDate();
        }
        images.clear();
        addAll(newStaccatoImages, staccato);
    }

    private boolean isImagesChanged(StaccatoImages newStaccatoImages) {
        return isImagesSizeChanged(newStaccatoImages) || isImagesNotSameOrder(newStaccatoImages);
    }

    private boolean isImagesSizeChanged(StaccatoImages newStaccatoImages) {
        return this.images.size() != newStaccatoImages.images.size();
    }

    private boolean isImagesNotSameOrder(StaccatoImages newStaccatoImages) {
        for (int i = 0; i < this.images.size(); i++) {
            String oldUrl = this.images.get(i).getImageUrl();
            String newUrl = newStaccatoImages.images.get(i).getImageUrl();
            if (!oldUrl.equals(newUrl)) {
                return true;
            }
        }
        return false;
    }

    public void addAll(StaccatoImages newStaccatoImages, Staccato staccato) {
        newStaccatoImages.images.forEach(image -> {
            this.images.add(image);
            image.assignTo(staccato);
        });
    }
}
