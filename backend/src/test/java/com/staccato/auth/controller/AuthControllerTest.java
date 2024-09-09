package com.staccato.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.staccato.auth.service.AuthService;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;

    @DisplayName("유효한 로그인 요청이 들어오면 성공 응답을 한다.")
    @Test
    void login() throws Exception {
        // given
        String loginRequest = """
                {
                  "nickname": "staccato"
                }
                """;
        String expectedResponse = """
                {
                  "token": "staccatotoken"
                }
                """;
        LoginResponse loginResponse = new LoginResponse("staccatotoken");
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("닉네임을 입력하지 않으면 400을 반환한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"\"\"", "\" \""})
    void cannotLoginIfBadRequest(String nickname) throws Exception {
        // given
        String loginRequest = "{"
                + "\"nickname\": " + nickname
                + "}";
        String expectedResponse = """
                {
                  "status": "400 BAD_REQUEST",
                  "message": "닉네임을 입력해주세요."
                }
                """;

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("20자를 초과하면 400을 반환한다.")
    @Test
    void cannotLoginIfLengthExceeded() throws Exception {
        // given
        String nickname = "가".repeat(21);
        String loginRequest = "{"
                + "\"nickname\": \"" + nickname + "\""
                + "}";
        String expectedResponse = """
                {
                  "status": "400 BAD_REQUEST",
                  "message": "1자 이상 20자 이하의 닉네임으로 설정해주세요."
                }
                """;

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponse));
    }
}
