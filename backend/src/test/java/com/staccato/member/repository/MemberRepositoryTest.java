package com.staccato.member.repository;

import com.staccato.RepositoryTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("닉네임 목록에 포함된 모든 멤버를 조회한다.")
    @Test
    void findAllByNicknameIn() {
        // given
        MemberFixtures.defaultMember()
                .withNickname("user1")
                .buildAndSave(memberRepository);
        MemberFixtures.defaultMember()
                .withNickname("user2")
                .buildAndSave(memberRepository);
        MemberFixtures.defaultMember()
                .withNickname("user3")
                .buildAndSave(memberRepository);

        List<String> nicknames = List.of("user1", "user3");

        // when
        List<Member> result = memberRepository.findAllByNicknameIn(nicknames);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(member -> member.getNickname().getNickname()))
                .containsExactlyInAnyOrder("user1", "user3");
    }

    @DisplayName("닉네임 목록에 포함된 멤버가 아무도 없으면 빈 리스트를 반환한다.")
    @Test
    void findAllByNicknameInNoMatch() {
        // given
        MemberFixtures.defaultMember()
                .withNickname("user1")
                .buildAndSave(memberRepository);

        // when
        List<Member> result = memberRepository.findAllByNicknameIn(List.of("nonexistent"));

        // then
        assertThat(result).isEmpty();
    }
}
