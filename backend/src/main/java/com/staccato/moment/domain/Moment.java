package com.staccato.moment.domain;

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
import com.staccato.memory.domain.Memory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Moment extends BaseEntity {
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
    @JoinColumn(name = "memory_id", nullable = false)
    private Memory memory;
    @Embedded
    private MomentImages momentImages = new MomentImages();

    @Builder
    public Moment(
            @NonNull LocalDateTime visitedAt,
            @NonNull String title,
            @NonNull String placeName,
            @NonNull String address,
            @NonNull BigDecimal latitude,
            @NonNull BigDecimal longitude,
            @NonNull MomentImages momentImages,
            @NonNull Memory memory
    ) {
        validateIsWithinMemoryDuration(visitedAt, memory);
        this.visitedAt = visitedAt.truncatedTo(ChronoUnit.SECONDS);
        this.title = title.trim();
        this.spot = new Spot(placeName, address, latitude, longitude);
        this.momentImages.addAll(momentImages, this);
        this.memory = memory;
    }

    private void validateIsWithinMemoryDuration(LocalDateTime visitedAt, Memory memory) {
        if (memory.isWithoutDuration(visitedAt)) {
            throw new StaccatoException("추억에 포함되지 않는 날짜입니다.");
        }
    }

    public void update(Moment newMoment) {
        this.visitedAt = newMoment.getVisitedAt();
        this.title = newMoment.getTitle();
        this.spot = newMoment.getSpot();
        this.momentImages.update(newMoment.momentImages, this);
        this.memory = newMoment.getMemory();
    }

    public String thumbnailUrl() {
        if (hasImage()) {
            return momentImages.getImages().get(0).getImageUrl();
        }
        return null;
    }

    private boolean hasImage() {
        return momentImages.isNotEmpty();
    }

    public void changeFeeling(Feeling feeling) {
        this.feeling = feeling;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void touchForWrite() {
        memory.setUpdatedAt(LocalDateTime.now());
    }

    public List<MomentImage> existingImages() {
        return momentImages.getImages();
    }
}
