package com.staccato.fixture.staccato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Spot;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.domain.StaccatoImages;
import com.staccato.staccato.repository.StaccatoRepository;

public class StaccatoFixtures {

    public static StaccatoBuilder defaultStaccato() {
        return new StaccatoBuilder()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withTitle("staccatoTitle")
                .withSpot(BigDecimal.ZERO.setScale(14), BigDecimal.ZERO.setScale(14))
                .withCreatedBy(1L)
                .withModifiedBy(1L);
    }

    public static class StaccatoBuilder {
        Long id;
        LocalDateTime visitedAt;
        String title;
        Spot spot;
        Category category;
        StaccatoImages staccatoImages = new StaccatoImages(List.of());
        Long createdBy;
        Long modifiedBy;

        public StaccatoBuilder withVisitedAt(LocalDateTime visitedAt) {
            this.visitedAt = visitedAt.truncatedTo(ChronoUnit.SECONDS);
            return this;
        }

        public StaccatoBuilder withTitle(String title) {
            this.title = title.trim();
            return this;
        }

        public StaccatoBuilder withSpot(BigDecimal latitude, BigDecimal longitude) {
            this.spot = new Spot("placeName", "address", latitude, longitude);
            return this;
        }

        public StaccatoBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public StaccatoBuilder withStaccatoImages(List<String> staccatoImages) {
            this.staccatoImages = new StaccatoImages(staccatoImages);
            return this;
        }

        public StaccatoBuilder withCreator(Member auditor) {
            this.createdBy = auditor.getId();
            this.modifiedBy = auditor.getId();
            return this;
        }

        public StaccatoBuilder withCreatedBy(Long createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public StaccatoBuilder withModifiedBy(Long modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public Staccato build() {
            return new Staccato(visitedAt, title, spot.getPlaceName(), spot.getAddress(), spot.getLatitude(),
                    spot.getLongitude(), staccatoImages, category, createdBy, modifiedBy);
        }

        public Staccato buildAndSave(StaccatoRepository repository) {
            Staccato staccato = build();
            return repository.save(staccato);
        }

        public Staccato buildAndSaveWithStaccatoImages(List<String> staccatoImages, StaccatoRepository repository) {
            Staccato staccato = build();
            this.staccatoImages.addAll(new StaccatoImages(staccatoImages), staccato);
            return repository.save(staccato);
        }
    }
}
