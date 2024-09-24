package com.staccato.fixture.moment;

import java.util.List;

import com.staccato.moment.service.dto.request.MomentUpdateRequest;

public class MomentUpdateRequestFixture {
    public static MomentUpdateRequest create() {
        return new MomentUpdateRequest("staccatoTitle", List.of("https://example1.com.jpg"));
    }

    public static MomentUpdateRequest create(String staccatoTitle) {
        return new MomentUpdateRequest(staccatoTitle, List.of("https://example1.com.jpg"));
    }
}
