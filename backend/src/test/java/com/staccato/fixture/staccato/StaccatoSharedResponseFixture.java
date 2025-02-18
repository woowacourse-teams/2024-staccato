package com.staccato.fixture.staccato;

import java.util.List;

import com.staccato.moment.service.dto.response.CommentShareResponse;
import com.staccato.moment.service.dto.response.StaccatoSharedResponse;

public class StaccatoSharedResponseFixture {
    public static StaccatoSharedResponse create() {
        return new StaccatoSharedResponse(
                "폭포",
                List.of(
                        "https://image.staccato.kr/dev/squirrel.png",
                        "https://image.staccato.kr/dev/squirrel.png",
                        "https://image.staccato.kr/dev/squirrel.png"
                ),
                "귀여운 스타카토 키링",
                "한국 루터회관 8층",
                "대한민국 서울특별시 송파구 올림픽로35다길 42 한국루터회관 8층",
                "2024-09-29T17:00:00.000Z",
                "scared",
                List.of(
                        new CommentShareResponse("폭포", "댓글 샘플", "image.jpg"),
                        new CommentShareResponse("폭포2", "댓글 샘플2", "image2.jpg")
                )
        );
    }
}
