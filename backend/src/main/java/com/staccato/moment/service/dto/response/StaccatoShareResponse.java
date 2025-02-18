package com.staccato.moment.service.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토를 공유했을 때 응답 형식입니다.")
public record StaccatoShareResponse(
        @Schema(example = "폭포")
        String userName,
        @ArraySchema(arraySchema = @Schema(example = "[" +
                "\"https://image.staccato.kr/dev/squirrel.png\"," +
                "\"https://image.staccato.kr/dev/squirrel.png\"," +
                "\"https://image.staccato.kr/dev/squirrel.png\"," +
                "\"https://image.staccato.kr/dev/squirrel.png\"," +
                "\"https://image.staccato.kr/dev/%E1%84%80%E1%85%B5%E1%84%87%E1%85%AE%E1%84%82%E1%85%B5%E1%84%83%E1%85%B3%E1%86%AF.jpeg\"]"))
        List<String> staccatoImageUrls,
        @Schema(example = "귀여운 스타카토 키링")
        String staccatoTitle,
        @Schema(example = "한국 루터회관 8층")
        String placeName,
        @Schema(example = "대한민국 서울특별시 송파구 올림픽로35다길 42 한국루터회관 8층")
        String address,
        @Schema(example = "2024-09-29T17:00:00.000Z")
        String visitedAt,
        @Schema(example = "scared")
        String feeling,
        // 기존에 있는 CommentResponses DTO 사용 예정. 데이터 구성 방식이 실제와 많이 달라서,
        // 잘 되는지만 테스트하기 위해 임시로 CommentShareResponse DTO 만들어서 구현. 나중에 삭제 예정
        List<CommentShareResponse> comments
) {
    public static final String FIRST_COMMENT_CONTENT = "나랏말싸미 듕귁에 달아 문자와로 서로 사맛디 아니할쎄\n" +
            "이런 젼차로 어린 백셩이 니르고져 홇베이셔도\n" +
            "마참내 제 뜨들 시러펴디 몯핧 노미하니라\n" +
            "내 이랄 윙하여 어엿비너겨 새로 스믈 여덟 짜랄 맹가노니\n" +
            "사람마다 해여 수비니겨 날로 쑤메 뼌한킈 하고져 할따라미니라.";
    public static final String SECOND_COMMENT_CONTENT = "우리 나라 말이 중국과 달라 한자와는 서로 말이 통하지 아니하여서\n" +
            "이런 까닭으로 어리석은 백성이 말하고자 하는 바가 있어도\n" +
            "마침내 제 뜻을 펴지 못하는 사람이 많다.\n" +
            "내가 이것을 가엾게 여겨 새로 스물 여덟 글자를 만드니\n" +
            "모든 사람들로 하여금 쉽게 익혀서 날마다 쓰는 게 편하게 하고자 할 따름이니라.";
    public static final String THIRD_COMMENT_CONTENT = "짧은 코멘트 테스트";

    public StaccatoShareResponse() {
        this(
                "폭포",
                List.of(
                        "https://image.staccato.kr/dev/squirrel.png",
                        "https://image.staccato.kr/dev/squirrel.png",
                        "https://image.staccato.kr/dev/squirrel.png",
                        "https://image.staccato.kr/dev/squirrel.png",
                        "https://image.staccato.kr/dev/%E1%84%80%E1%85%B5%E1%84%87%E1%85%AE%E1%84%82%E1%85%B5%E1%84%83%E1%85%B3%E1%86%AF.jpeg"
                ),
                "귀여운 스타카토 키링",
                "한국 루터회관 8층",
                "대한민국 서울특별시 송파구 올림픽로35다길 42 한국루터회관 8층",
                "2024-09-29T17:00:00.000Z",
                "scared",
                List.of(
                        new CommentShareResponse("낙낙", FIRST_COMMENT_CONTENT, "https://image.staccato.kr/dev/naknak.png"),
                        new CommentShareResponse("폭포", SECOND_COMMENT_CONTENT, "https://image.staccato.kr/dev/squirrel.png"),
                        new CommentShareResponse("폭포", THIRD_COMMENT_CONTENT, "https://image.staccato.kr/dev/squirrel.png")
                )
        );
    }
}
