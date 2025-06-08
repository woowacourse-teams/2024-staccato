package com.staccato.notification.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import com.staccato.ControllerTest;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.member.MemberFixtures;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest extends ControllerTest {

    @DisplayName("알림용 토큰을 등록합니다.")
    @Test
    void register() throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());

        // when & then
        mockMvc.perform(post("/notification/token")
                        .param("token", "FCM_Token")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("알림용 토큰이 공백으로 전달되는 경우 등록에 실패합니다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void failRegister(String token) throws Exception {
        // given
        when(authService.extractFromToken(anyString())).thenReturn(MemberFixtures.defaultMember().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "토큰 값은 필수이며, 공백일 수 없습니다.");

        // when & then
        mockMvc.perform(post("/notification/token")
                        .param("token", token)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
