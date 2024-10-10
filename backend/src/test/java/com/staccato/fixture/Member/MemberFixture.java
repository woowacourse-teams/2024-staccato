package com.staccato.fixture.Member;

import com.staccato.member.domain.Member;

public class MemberFixture {
    public static Member create() {
        return Member.create("staccato");
    }

    public static Member create(String nickname) {
        return Member.create(nickname);
    }
}
