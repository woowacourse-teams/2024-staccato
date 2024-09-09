package com.staccato.fixture.Member;

import java.util.Objects;

public class LoginRequestFixture {
    public static String create(String nickname) {
        if (Objects.isNull(nickname)) {
            return "{}";
        }
        return "{"
                + "\"nickname\": \"" + nickname + "\""
                + "}";
    }
}
