package com.staccato.fixture.auth;

import com.staccato.auth.service.dto.request.LoginRequest;

public class LoginRequestFixture {
    public static LoginRequest create(){
        return new LoginRequest("staccato");
    }
}
