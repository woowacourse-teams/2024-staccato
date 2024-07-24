package com.staccato.travel.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.staccato.IntegrationTest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.service.dto.request.TravelRequest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class TravelIntegrationTest extends IntegrationTest {
    private static final String USER_AUTHORIZATION = "1";

    @Autowired
    private MemberRepository memberRepository;

    static Stream<TravelRequest> travelRequestProvider() {
        return Stream.of(
                new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new TravelRequest(null, "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", null, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10))
        );
    }

    static Stream<Arguments> invalidTravelRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", null, "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "여행 제목을 입력해주세요."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", null, LocalDate.of(2023, 7, 10)),
                        "여행 시작 날짜를 입력해주세요."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), null),
                        "여행 끝 날짜를 입력해주세요."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "가".repeat(31), "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "제목의 최대 허용 글자수는 공백 포함 30자입니다."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "가".repeat(501), LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)),
                        "내용의 최대 허용 글자수는 공백 포함 500자입니다."
                ),
                Arguments.of(
                        new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 10), LocalDate.of(2023, 7, 1)),
                        "끝 날짜가 시작 날짜보다 앞설 수 없어요."
                )
        );
    }

    @BeforeEach
    void init() {
        memberRepository.save(Member.builder().nickname("staccato").build());
    }

    @DisplayName("사용자가 여행 상세 정보를 입력하면, 새로운 여행 상세를 생성한다.")
    @ParameterizedTest
    @MethodSource("travelRequestProvider")
    void createTravel(TravelRequest travelRequest) {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(travelRequest)
                .when().log().all()
                .post("/travels")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/travels/"));
    }

    @DisplayName("사용자가 잘못된 방식으로 정보를 입력하면, 여행 상세를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidTravelRequestProvider")
    void failCreateTravel(TravelRequest travelRequest, String expectedMessage) {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(travelRequest)
                .when().log().all()
                .post("/travels")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is(expectedMessage))
                .body("status", is(HttpStatus.BAD_REQUEST.toString()));
    }

    @DisplayName("사용자가 여행 상세 정보를 입력하면, 여행 상세를 수정한다.")
    @ParameterizedTest
    @MethodSource("travelRequestProvider")
    void updateTravel(TravelRequest travelRequest) {
        // given
        createTravel(travelRequest);
        Long travelId = 1L;

        // when & then
        RestAssured.given().pathParam("travelId", travelId).log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(travelRequest)
                .when().log().all()
                .put("/travels/{travelId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("사용자가 잘못된 방식으로 정보를 입력하면, 여행 상세를 수정할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidTravelRequestProvider")
    void failUpdateTravel(TravelRequest invalidTravelRequest, String expectedMessage) {
        // given
        Long travelId = 1L;

        // when & then
        RestAssured.given().pathParam("travelId", travelId).log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(invalidTravelRequest)
                .when().log().all()
                .put("/travels/{travelId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is(expectedMessage))
                .body("status", is(HttpStatus.BAD_REQUEST.toString()));
    }

    @DisplayName("사용자가 잘못된 여행식별자로 접근한다면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void failAccessTravel(Long travelId) {
        // given
        TravelRequest travelRequest = new TravelRequest("https://example.com/travels/geumohrm.jpg", "2023 여름 휴가", "친구들과 함께한 여름 휴가 여행", LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10));
        String expectedMessage = "여행 식별자는 양수로 이루어져야 합니다.";

        // when & then
        RestAssured.given().pathParam("travelId", travelId).log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(travelRequest)
                .when().log().all()
                .put("/travels/{travelId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is(expectedMessage))
                .body("status", is(HttpStatus.BAD_REQUEST.toString()));
    }
}
