package com.staccato.moment.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;

import com.staccato.comment.domain.Comment;
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
    @OneToMany(mappedBy = "moment", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Moment(
            @NonNull LocalDateTime visitedAt,
            @NonNull String title,
            @NonNull String address,
            @NonNull BigDecimal latitude,
            @NonNull BigDecimal longitude,
            @NonNull MomentImages momentImages,
            @NonNull Memory memory
    ) {
        validateIsWithinMemoryDuration(visitedAt, memory);
        this.visitedAt = visitedAt.truncatedTo(ChronoUnit.SECONDS);
        this.title = title.trim();
        this.spot = new Spot(address, latitude, longitude);
        this.momentImages.addAll(momentImages, this);
        this.memory = memory;
    }

    private void validateIsWithinMemoryDuration(LocalDateTime visitedAt, Memory memory) {
        if (memory.isWithoutDuration(visitedAt)) {
            throw new StaccatoException("추억에 포함되지 않는 날짜입니다.");
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void update(String title, MomentImages newMomentImages) {
        this.title = title;
        this.momentImages.update(newMomentImages, this);
    }

    public void update(Moment updatedMoment) {
        this.visitedAt = updatedMoment.getVisitedAt();
        this.title = updatedMoment.getTitle();
        this.spot = updatedMoment.getSpot();
        this.momentImages.update(updatedMoment.momentImages, this);
        this.memory = updatedMoment.getMemory();
    }

    public String getThumbnailUrl() {
        return momentImages.getImages().get(0).getImageUrl();
    }

    public boolean hasImage() {
        return momentImages.isNotEmpty();
    }

    public void changeFeeling(Feeling feeling) {
        this.feeling = feeling;
    }
}
