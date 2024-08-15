package com.staccato.moment.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

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
    private String placeName;
    @Column(nullable = false)
    @Embedded
    private Spot spot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id", nullable = false)
    private Memory memory;
    @Embedded
    private MomentImages momentImages = new MomentImages();
    @OneToMany(mappedBy = "moment", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Moment(
            @NonNull LocalDateTime visitedAt,
            @NonNull String placeName,
            @NonNull String address,
            @NonNull BigDecimal latitude,
            @NonNull BigDecimal longitude,
            @NonNull Memory memory
    ) {
        validateIsWithinTravelDuration(visitedAt, memory);
        this.visitedAt = visitedAt;
        this.placeName = placeName;
        this.spot = new Spot(address, latitude, longitude);
        this.memory = memory;
    }

    private void validateIsWithinTravelDuration(LocalDateTime visitedAt, Memory memory) {
        if (memory.isWithoutDuration(visitedAt)) {
            throw new StaccatoException("여행에 포함되지 않는 날짜입니다.");
        }
    }

    public void addMomentImages(MomentImages momentImages) {
        this.momentImages.addAll(momentImages, this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void update(String placeName, MomentImages newMomentImages) {
        this.placeName = placeName;
        this.momentImages.update(newMomentImages, this);
    }

    public String getThumbnailUrl() {
        return momentImages.getImages().get(0).getImageUrl();
    }

    public boolean hasImage() {
        return momentImages.isNotEmpty();
    }
}
