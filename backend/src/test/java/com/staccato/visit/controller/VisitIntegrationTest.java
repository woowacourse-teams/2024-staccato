package com.staccato.visit.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.staccato.IntegrationTest;
import com.staccato.pin.domain.Pin;
import com.staccato.pin.repository.PinRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.service.dto.request.VisitRequest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class VisitIntegrationTest extends IntegrationTest {
    private static final String USER_AUTHORIZATION = "1";

    @Autowired
    private PinRepository pinRepository;
    @Autowired
    private TravelRepository travelRepository;

    static Stream<Arguments> invalidVisitRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new VisitRequest(null, List.of("https://example1.com.jpg"), LocalDate.of(2023, 7, 1), 1L),
                        "핀을 선택해주세요."
                ),
                Arguments.of(
                        new VisitRequest(1L, List.of("https://example1.com.jpg"), null, 1L),
                        "방문 날짜를 입력해주세요."
                ),
                Arguments.of(
                        new VisitRequest(1L, List.of("https://example1.com.jpg"), LocalDate.of(2023, 7, 1), null),
                        "여행 상세를 선택해주세요."
                )
        );
    }

    @BeforeEach
    void init() {
        pinRepository.save(Pin.builder().place("장소").address("주소").build());
        travelRepository.save(Travel.builder()
                .thumbnailUrl("https://example1.com.jpg")
                .title("2023 여름 휴가")
                .description("친구들과 함께한 여름 휴가 여행")
                .startAt(LocalDate.of(2023, 7, 1))
                .endAt(LocalDate.of(2023, 7, 10))
                .build());
    }

    @DisplayName("사용자가 방문 기록 정보를 입력하면, 새로운 방문 기록을 생성한다.")
    @Test
    void createVisit() {
        // given
        VisitRequest visitRequest = new VisitRequest(1L, List.of("https://example1.com.jpg"),
                LocalDate.of(2023, 7, 1), 1L);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(visitRequest)
                .when().log().all()
                .post("/visits")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/visits/"));
    }

    @DisplayName("사용자가 잘못된 방식으로 정보를 입력하면, 방문 기록을 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidVisitRequestProvider")
    void failCreateTravel(VisitRequest visitRequest, String expectedMessage) {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(visitRequest)
                .when().log().all()
                .post("/visits")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is(expectedMessage))
                .body("status", is(HttpStatus.BAD_REQUEST.toString()));
    }

    @DisplayName("Visit을 삭제한다.")
    @Test
    void deleteById() {
        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "1")
                .contentType(ContentType.JSON)
                .when().delete("/visits/1")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("1보다 작은 id로 Visit 삭제를 시도하면 Bad Request를 반환한다.")
    @Test
    void deleteByIdFail() {
        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "1")
                .contentType(ContentType.JSON)
                .when().delete("/visits/0")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is("방문 기록 식별자는 양수로 이루어져야 합니다."))
                .body("status", is(HttpStatus.BAD_REQUEST.toString()));
    }
}
