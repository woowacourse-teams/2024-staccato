package com.staccato.visit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VisitControllerTest {
    @DisplayName("Visit을 삭제한다.")
    @Test
    void deleteById() {
        RestAssured.given().log().all()
                .header("Authorization", "1")
                .contentType(ContentType.JSON)
                .when().delete("/visits/1")
                .then().log().all()
                .statusCode(204);
    }
}
