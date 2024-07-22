package com.staccato.travel.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.util.DatabaseCleanerExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@ExtendWith(DatabaseCleanerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TravelAcceptanceTest {
    private static final String USER_AUTHORIZATION = "1";
    private static final String BAD_REQUEST_STATUS = "400 BAD_REQUEST";

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;

        memberRepository.save(Member.builder().nickname("staccato").build());
    }

    @DisplayName("사용자가 여행 상세 정보를 입력하면, 새로운 여행 상세를 생성한다.")
    @Test
    void createTravel() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );

        // when & then
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

    @DisplayName("사용자가 썸네일 없이 여행 상세 정보를 입력하면, 새로운 여행 상세를 생성한다.")
    @Test
    void createTravelWithoutThumbnail() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                null,
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );

        // when & then
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

    @DisplayName("사용자가 썸네일 없이 여행 상세 정보를 입력하면, 새로운 여행 상세를 생성한다.")
    @Test
    void createTravelWithoutDescription() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                null,
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );

        // when & then
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

    @DisplayName("사용자가 제목 없이 여행 상세를 생성할 수 없다.")
    @Test
    void failCreateTravelWithoutTitle() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                null,
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(travelRequest)
                .when().log().all()
                .post("/travels")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is("여행 제목을 입력해주세요."))
                .body("status", is(BAD_REQUEST_STATUS));
    }

    @DisplayName("사용자가 시작 날짜 없이 여행 상세를 생성할 수 없다.")
    @Test
    void failCreateTravelWithoutStartDate() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                null,
                LocalDate.of(2023, 7, 10)
        );

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(travelRequest)
                .when().log().all()
                .post("/travels")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is("여행 시작 날짜를 입력해주세요."))
                .body("status", is(BAD_REQUEST_STATUS));
    }

    @DisplayName("사용자가 끝 날짜 없이 여행 상세를 생성할 수 없다.")
    @Test
    void failCreateTravelWithoutEndDate() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                null
        );

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, USER_AUTHORIZATION)
                .body(travelRequest)
                .when().log().all()
                .post("/travels")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is("여행 끝 날짜를 입력해주세요."))
                .body("status", is(BAD_REQUEST_STATUS));
    }
}
