package com.staccato.staccato.domain;

import com.staccato.category.domain.Category;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import com.staccato.config.domain.BaseEntity;
import com.staccato.exception.StaccatoException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Staccato extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime visitedAt;
    @Column(nullable = false)
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Feeling feeling = Feeling.NOTHING;
    @Column(nullable = false)
    @Embedded
    private Spot spot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Embedded
    private StaccatoImages staccatoImages = new StaccatoImages();

    @Builder
    public Staccato(
            @NonNull LocalDateTime visitedAt,
            @NonNull String title,
            @NonNull String placeName,
            @NonNull String address,
            @NonNull BigDecimal latitude,
            @NonNull BigDecimal longitude,
            @NonNull StaccatoImages staccatoImages,
            @NonNull Category category
    ) {
        validateIsWithinCategoryTerm(visitedAt, category);
        this.visitedAt = visitedAt.truncatedTo(ChronoUnit.SECONDS);
        this.title = title.trim();
        this.spot = new Spot(placeName, address, latitude, longitude);
        this.staccatoImages.addAll(staccatoImages, this);
        this.category = category;
    }

    private void validateIsWithinCategoryTerm(LocalDateTime visitedAt, Category category) {
        if (category.isWithoutDuration(visitedAt)) {
            throw new StaccatoException("카테고리에 포함되지 않는 날짜입니다.");
        }
    }

    public void update(Staccato newStaccato) {
        this.visitedAt = newStaccato.getVisitedAt();
        this.title = newStaccato.getTitle();
        this.spot = newStaccato.getSpot();
        this.staccatoImages.update(newStaccato.staccatoImages, this);
        this.category = newStaccato.getCategory();
    }

    public String thumbnailUrl() {
        if (hasImage()) {
            return staccatoImages.getImages().get(0).getImageUrl();
        }
        return null;
    }

    private boolean hasImage() {
        return staccatoImages.isNotEmpty();
    }

    public void changeFeeling(Feeling feeling) {
        this.feeling = feeling;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void touchForWrite() {
        category.setUpdatedAt(LocalDateTime.now());
    }

    public List<StaccatoImage> existingImages() {
        return staccatoImages.getImages();
    }
}
