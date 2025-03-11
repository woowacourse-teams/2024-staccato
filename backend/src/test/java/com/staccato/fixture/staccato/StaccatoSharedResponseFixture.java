package com.staccato.fixture.staccato;

import java.time.LocalDateTime;
import java.util.List;

import com.staccato.moment.service.dto.response.CommentShareResponse;
import com.staccato.moment.service.dto.response.StaccatoSharedResponse;

public class StaccatoSharedResponseFixture {
    public static StaccatoSharedResponse create() {
        return new StaccatoSharedResponse(
                1,
                LocalDateTime.of(2024, 7, 2, 10, 0),
                "staccato",
                List.of(
                        "https://oldExample.com.jpg",
                        "https://existExample.com.jpg"
                ),
                "staccatoTitle",
                "placeName",
                "address",
                LocalDateTime.of(2024, 7, 1, 10, 0),
                "nothing",
                List.of(
                        new CommentShareResponse("staccato", "댓글 샘플", "image.jpg"),
                        new CommentShareResponse("staccato2", "댓글 샘플2", "image2.jpg")
                )
        );
    }
}
