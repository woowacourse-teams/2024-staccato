package com.staccato.config.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;

public class MemberTokenProviderTest extends ServiceSliceTest {

    @Autowired
    private MemberRepository memberRepository;

    private MemberTokenProvider tokenProvider;
    private Member member;

    @BeforeEach
    public void setUp() {
        TokenProperties tokenProperties = new TokenProperties("test-secret-key");
        tokenProvider = new MemberTokenProvider(tokenProperties);
        member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
    }

    @DisplayName("멤버 토큰은 멤버 아이디를 갖고 있다.")
    @Test
    void shareTokenContainsStaccatoId() {
        // given
        String token = tokenProvider.create(member);

        // when
        Claims claims = tokenProvider.getPayload(token);

        // then
        Assertions.assertThat(claims.get("id", Long.class)).isEqualTo(member.getId());
    }

    @DisplayName("멤버 토큰에서 멤버 아이디를 추출할 수 있다.")
    @Test
    void extractStaccatoId() {
        // given
        String token = tokenProvider.create(member);

        // when
        long memberId = tokenProvider.extractMemberId(token);

        // then
        Assertions.assertThat(memberId).isEqualTo(member.getId());
    }
}
