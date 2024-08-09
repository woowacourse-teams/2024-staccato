package com.staccato.auth.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @DisplayName("유효한 로그인 요청이 들어오면 성공 응답을 한다.")
    @Test
    void login() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("staccato");
        LoginResponse loginResponse = new LoginResponse("staccatotoken");
        when(authService.login(loginRequest)).thenReturn(loginResponse);

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(loginResponse)));
    }

    @DisplayName("닉네임을 입력하지 않으면 400을 반환한다.")
    @Test
    void cannotLoginIfBadRequest() throws Exception {
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

    @DisplayName("닉네임을 입력하지 않거나 20자를 초과하면 400을 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 30})
    void cannotLoginIfBadRequest(int count) throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("가".repeat(count));
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "1자 이상 20자 이하의 닉네임으로 설정해주세요.");

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
