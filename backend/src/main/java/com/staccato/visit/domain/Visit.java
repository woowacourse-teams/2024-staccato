package com.staccato.visit.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.staccato.config.domain.BaseEntity;
import com.staccato.travel.domain.Travel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE visit SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Visit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate visitedAt;
    @Column(nullable = false)
    private String placeName;
    @Column(nullable = false)
    @Embedded
    private Spot spot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;
    @Embedded
    private VisitImages visitImages = new VisitImages();
    @OneToMany(mappedBy = "visit", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<VisitLog> visitLogs = new ArrayList<>();

    @Builder
    public Visit(
            @NonNull LocalDate visitedAt,
            @NonNull String placeName,
            @NonNull String address,
            @NonNull BigDecimal latitude,
            @NonNull BigDecimal longitude,
            @NonNull Travel travel
    ) {
        this.visitedAt = visitedAt;
        this.placeName = placeName;
        this.spot = new Spot(address, latitude, longitude);
        this.travel = travel;
    }

    public void addVisitImages(VisitImages visitImages) {
        this.visitImages.addAll(visitImages, this);
    }

    public void addVisitLog(VisitLog visitLog) {
        this.visitLogs.add(visitLog);
    }

    public void update(String placeName, VisitImages newVisitImages) {
        this.placeName = placeName;
        this.visitImages.update(newVisitImages, this);
    }
}
