package com.staccato.fixture.staccato;

import com.staccato.staccato.service.dto.request.StaccatoRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class StaccatoRequestFixture {

    public static StaccatoRequest create(Long categoryId) {
        return new StaccatoRequest(
                "staccatoTitle",
                "placeName",
                "address",
                BigDecimal.ONE,
                BigDecimal.ONE,
                LocalDateTime.now(),
                categoryId,
                List.of());
    }

    public static StaccatoRequest create(long categoryId, List<String> staccatoImages) {
        return new StaccatoRequest(
                "staccatoTitle",
                "placeName",
                "address",
                BigDecimal.ONE,
                BigDecimal.ONE,
                LocalDateTime.now(),
                categoryId,
                staccatoImages);
    }
}
