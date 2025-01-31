package com.staccato.moment.domain;

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
public class MomentImages {
    private static final int MAX_COUNT = 5;
    @OneToMany(mappedBy = "moment", cascade = CascadeType.PERSIST)
    private List<MomentImage> images = new ArrayList<>();

    public MomentImages(List<String> addedImages) {
        validateNumberOfImages(addedImages);
        this.images.addAll(addedImages.stream()
                .map(MomentImage::new)
                .toList());
    }

    private void validateNumberOfImages(List<String> addedImages) {
        if (addedImages.size() > MAX_COUNT) {
            throw new StaccatoException("사진은 5장을 초과할 수 없습니다.");
        }
    }

    protected void addAll(MomentImages newMomentImages, Moment moment) {
        newMomentImages.images.forEach(image -> {
            this.images.add(image);
            image.belongTo(moment);
        });
    }

    public boolean isNotEmpty() {
        return !images.isEmpty();
    }

    protected void update(MomentImages newMomentImages, Moment moment) {
        removeExistImages(newMomentImages);
        saveNewImages(newMomentImages, moment);
    }

    private void removeExistImages(MomentImages newMomentImages) {
        List<MomentImage> momentImages = findImagesNotPresentIn(newMomentImages);
        images.removeAll(momentImages);
    }

    private void saveNewImages(MomentImages newMomentImages, Moment moment) {
        List<MomentImage> momentImages = newMomentImages.findImagesNotPresentIn(this);
        momentImages.forEach(image -> {
            this.images.add(image);
            image.belongTo(moment);
        });
    }

    public List<Long> findRemovalImageIds(MomentImages newMomentImages) {
        List<MomentImage> momentImages = findImagesNotPresentIn(newMomentImages);
        return momentImages.stream()
                .map(MomentImage::getId)
                .toList();
    }

    private List<MomentImage> findImagesNotPresentIn(MomentImages targetMomentImages) {
        return images.stream()
                .filter(image -> !targetMomentImages.contains(image))
                .toList();
    }

    private boolean contains(MomentImage momentImage) {
        return this.images.stream()
                .anyMatch(image -> image.isSame(momentImage));
    }
}
