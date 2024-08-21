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
    @OneToMany(mappedBy = "moment", orphanRemoval = true, cascade = CascadeType.ALL)
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

    protected void update(MomentImages momentImages, Moment moment) {
        removeExistsImages(new ArrayList<>(images));
        addAll(momentImages, moment);
    }

    private void removeExistsImages(List<MomentImage> originalImages) {
        originalImages.forEach(this.images::remove);
    }

    public boolean isNotEmpty() {
        return !images.isEmpty();
    }
}
