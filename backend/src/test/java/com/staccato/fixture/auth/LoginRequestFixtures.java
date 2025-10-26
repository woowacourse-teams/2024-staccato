package com.staccato.fixture.auth;

import com.staccato.auth.service.dto.request.LoginRequest;

public class LoginRequestFixtures {

    public static LoginRequestBuilder ofDefault() {
        return new LoginRequestBuilder()
                .withNickname("nickname");
    }

    public static class LoginRequestBuilder {
        String nickname;

        public LoginRequestBuilder withNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public LoginRequest build() {
            return new LoginRequest(nickname);
        }
    }
}
