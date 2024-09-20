package com.staccato.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.member.service.dto.response.MemberProfileResponse;


class MemberServiceTest extends ServiceSliceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @DisplayName("주어진 이미지 경로로 사용자의 프로필 사진을 변경한다.")
    @Test
    void changeProfileImage() {
        // given
        Member member = memberRepository.save(MemberFixture.create("staccato"));
        String imageUrl = "image.jpg";

        // when
        MemberProfileResponse memberProfileResponse = memberService.changeProfileImage(member, member.getId(), imageUrl);

        // then
        Member result = memberRepository.findById(member.getId()).get();
        assertAll(
                () -> assertThat(memberProfileResponse.profileImageUrl()).isEqualTo(imageUrl),
                () -> assertThat(result.getImageUrl()).isEqualTo(imageUrl)
        );
    }

    @DisplayName("사용자가 다른 사용자의 프로필을 변경할 수 없다.")
    @Test
    void cannotChangeUnknownProfileImage() {
        // given
        long unknownId = 0;
        Member member = memberRepository.save(MemberFixture.create("staccato"));
        String imageUrl = "image.jpg";

        // when
        assertThatThrownBy(() -> memberService.changeProfileImage(member, unknownId, imageUrl))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 사용자 정보를 찾을 수 없어요.");
    }

    @DisplayName("사용자가 다른 사용자의 프로필을 변경할 수 없다.")
    @Test
    void cannotChangeOtherProfileImage() {
        // given
        Member member = memberRepository.save(MemberFixture.create("staccato"));
        Member otherMember = memberRepository.save(MemberFixture.create("other"));
        String imageUrl = "image.jpg";

        // when
        assertThatThrownBy(() -> memberService.changeProfileImage(member, otherMember.getId(), imageUrl))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }
}
