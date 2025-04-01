package com.staccato.fixture.staccato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.staccato.staccato.service.dto.request.StaccatoRequest;

public class StaccatoRequestFixtures {

    public static StaccatoBuilder defaultStaccatoRequest() {
        return new StaccatoBuilder()
                .withStaccatoTitle("staccatoTitle")
                .withPlaceName("placeName")
                .withAddress("address")
                .withLatitude(BigDecimal.ONE)
                .withLongitude(BigDecimal.ONE)
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0));
    }

    public static class StaccatoBuilder {
        String staccatoTitle;
        String placeName;
        String address;
        BigDecimal latitude;
        BigDecimal longitude;
        LocalDateTime visitedAt;
        long categoryId;
        List<String> staccatoImageUrls = List.of();

        public StaccatoBuilder withStaccatoTitle(String staccatoTitle) {
            this.staccatoTitle = staccatoTitle;
            return this;
        }

        public StaccatoBuilder withPlaceName(String placeName) {
            this.placeName = placeName;
            return this;
        }

        public StaccatoBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public StaccatoBuilder withLatitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public StaccatoBuilder withLongitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public StaccatoBuilder withVisitedAt(LocalDateTime visitedAt) {
            this.visitedAt = visitedAt;
            return this;
        }

        public StaccatoBuilder withCategoryId(long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public StaccatoBuilder withStaccatoImageUrls(List<String> staccatoImageUrls) {
            this.staccatoImageUrls = staccatoImageUrls;
            return this;
        }

        public StaccatoRequest build() {
            return new StaccatoRequest(staccatoTitle, placeName, address, latitude, longitude, visitedAt, categoryId, staccatoImageUrls);
        }
    }
}
