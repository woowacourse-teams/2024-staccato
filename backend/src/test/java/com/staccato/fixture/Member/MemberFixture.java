package com.staccato.fixture.Member;

import com.staccato.member.domain.Member;

public class MemberFixture {
    public static Member create() {
        return Member.builder().nickname("staccato").build();
    }

    public static Member create(String nickname) {
        return Member.builder().nickname(nickname).build();
    }
}
