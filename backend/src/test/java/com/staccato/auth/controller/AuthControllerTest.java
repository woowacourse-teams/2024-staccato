package com.staccato.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;
import com.staccato.exception.ExceptionResponse;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;

    @DisplayName("로그인 요청/응답에 대한 역직렬화/직렬화에 성공한다.")
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

    @DisplayName("닉네임이 1자 미만이면 400을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void cannotLoginIfNicknameTooShort(String nickname) throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(nickname);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "1자 이상 20자 이하의 닉네임으로 설정해주세요.");

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("닉네임을 입력하지 않으면 400을 반환한다.")
    @Test
    void cannotLoginIfNicknameNull() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(null);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "닉네임을 입력해주세요.");

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("20자를 초과하면 400을 반환한다.")
    @Test
    void cannotLoginIfLengthExceeded() throws Exception {
        // given
        String nickname = "가".repeat(21);
        LoginRequest loginRequest = new LoginRequest(nickname);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "1자 이상 20자 이하의 닉네임으로 설정해주세요.");

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("고유 코드로 사용자를 조회해서 토큰을 발급하는 응답을 직렬화한다.")
    @Test
    void findMemberByCodeAndCreateToken() throws Exception {
        // given
        String code = UUID.randomUUID().toString();
        LoginResponse loginResponse = new LoginResponse("token");
        when(authService.loginByCode(any(String.class))).thenReturn(loginResponse);
        String response = """
                {
                    "token" : "token"
                }
                """;

        // when & then
        mockMvc.perform(get("/members")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }
}
