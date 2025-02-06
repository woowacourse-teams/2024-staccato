package com.staccato.moment.domain;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MomentImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moment_id", nullable = false)
    private Moment moment;

    @Builder
    public MomentImage(@Nonnull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected void belongTo(Moment moment) {
        this.moment = moment;
    }

    protected boolean isSame(MomentImage momentImage) {
        return imageUrl.equals(momentImage.imageUrl);
    }
}
