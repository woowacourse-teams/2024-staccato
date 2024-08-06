package com.staccato.visit.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import com.staccato.exception.StaccatoException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitImages {
    private static final int MAX_COUNT = 5;
    @OneToMany(mappedBy = "visit", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<VisitImage> images = new ArrayList<>();

    public VisitImages(List<String> addedImages) {
        validateNumberOfImages(List.of(), addedImages);
        this.images.addAll(addedImages.stream()
                .map(VisitImage::new)
                .toList());
    }

    @Builder
    public VisitImages(List<String> existingImages, List<String> addedImages) {
        validateNumberOfImages(existingImages, addedImages);
        this.images.addAll(combine(existingImages, addedImages));
    }

    private void validateNumberOfImages(List<String> existingImages, List<String> addedImages) {
        if (existingImages.size() + addedImages.size() > MAX_COUNT) {
            throw new StaccatoException("사진은 5장까지만 추가할 수 있어요.");
        }
    }

    private List<VisitImage> combine(List<String> existingImages, List<String> addedImages) {
        List<String> images = new ArrayList(existingImages);
        images.addAll(addedImages);
        return images.stream()
                .map(VisitImage::new)
                .toList();
    }

    protected void addAll(VisitImages newVisitImages, Visit visit) {
        newVisitImages.images.forEach(image -> {
            this.images.add(image);
            image.belongTo(visit);
        });
    }

    protected void update(VisitImages visitImages, Visit visit) {
        removeOnlyOldImages(visitImages, new ArrayList<>(this.images));
        addOnlyNewImages(visitImages, visit);
    }

    private void removeOnlyOldImages(VisitImages visitImages, List<VisitImage> originalImages) {
        originalImages.stream()
                .filter(visitImages::without)
                .forEach(this.images::remove);
    }

    private void addOnlyNewImages(VisitImages visitImages, Visit visit) {
        visitImages.images.stream()
                .filter(this::without)
                .forEach(image -> {
                    this.images.add(image);
                    image.belongTo(visit);
                });
    }

    private boolean without(VisitImage image) {
        return this.images.stream()
                .noneMatch(visitImage -> visitImage.isSameUrl(image));
    }
}
