package com.staccato.auth.controller;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.staccato.ControllerTest;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponseV2;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.auth.LoginRequestFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTest {
    @DisplayName("로그인 요청/응답에 대한 역직렬화/직렬화에 성공한다.")
    @Test
    void login() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember().withNickname("staccato").build();
        String loginRequest = """
                {
                    "nickname": "staccato"
                }
                """;
        String expectedResponse = """
                {
                  "token": "staccatoToken"
                }
                """;
        LoginResponseV2 loginResponse = new LoginResponseV2(member, "staccatoToken");
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("닉네임이 공백만 있거나, 빈 문자열이거나, null이면 400을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void cannotLoginIfNicknameTooShort(String nickname) throws Exception {
        // given
        LoginRequest loginRequest = LoginRequestFixtures.defaultLoginRequest()
                .withNickname(nickname).build();
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "닉네임을 입력해주세요.");

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
        Member member = MemberFixtures.defaultMember().withCode(code).build();
        LoginResponseV2 loginResponse = new LoginResponseV2(member, "token");
        when(authService.loginByCode(any(String.class))).thenReturn(loginResponse);
        String response = """
                {
                    "token" : "token"
                }
                """;

        // when & then
        mockMvc.perform(post("/members")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }
}
