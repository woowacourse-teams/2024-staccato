package com.staccato.staccato.domain;

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
import jakarta.persistence.Version;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.config.domain.BaseEntity;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Staccato extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false)
    private LocalDateTime visitedAt;
    @Embedded
    private StaccatoTitle title;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Feeling feeling = Feeling.NOTHING;
    @Embedded
    private Spot spot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Embedded
    private StaccatoImages staccatoImages = new StaccatoImages();
    @Version
    private Long version;

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
        this.title = new StaccatoTitle(title);
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

    public void validateOwner(Member member) {
        if (category.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }

    public void validateCategoryChangeable(Category targetCategory) {
        if (category.getIsShared() || targetCategory.getIsShared()) {
            throw new StaccatoException("개인 카테고리 간에만 스타카토를 옮길 수 있어요.");
        }
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

    public void updateCategoryModifiedDate() {
        category.setUpdatedAt(LocalDateTime.now());
    }

    public Color getColor() {
        return category.getColor();
    }
}
