package com.staccato.fixture.moment;

import com.staccato.staccato.domain.Staccato;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.staccato.category.domain.Category;
import com.staccato.staccato.domain.StaccatoImages;

public class StaccatoFixture {
    private static final BigDecimal latitude = new BigDecimal("37.77490000000000");
    private static final BigDecimal longitude = new BigDecimal("-122.41940000000000");

    public static Staccato create(Category category) {
        return Staccato.builder()
                .visitedAt(LocalDateTime.of(2024, 7, 1, 10, 0))
                .title("staccatoTitle")
                .latitude(latitude)
                .longitude(longitude)
                .address("address")
                .placeName("placeName")
                .category(category)
                .staccatoImages(new StaccatoImages(List.of()))
                .build();
    }

    public static Staccato create(Category category, LocalDateTime visitedAt) {
        return Staccato.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .latitude(latitude)
                .longitude(longitude)
                .placeName("placeName")
                .address("address")
                .category(category)
                .staccatoImages(new StaccatoImages(List.of()))
                .build();
    }

    public static Staccato createWithImages(Category category, LocalDateTime visitedAt, List<String> staccatoImages) {
        return Staccato.builder()
            .visitedAt(visitedAt)
            .title("staccatoTitle")
            .latitude(latitude)
            .longitude(longitude)
            .placeName("placeName")
            .address("address")
            .category(category)
            .staccatoImages(new StaccatoImages(staccatoImages))
            .build();
    }
}
