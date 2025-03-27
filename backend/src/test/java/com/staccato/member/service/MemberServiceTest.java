package com.staccato.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.member.MemberFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.member.service.dto.response.MemberProfileImageResponse;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


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
        MemberProfileImageResponse memberProfileImageResponse = memberService.changeProfileImage(member, imageUrl);

        // then
        Member result = memberRepository.findById(member.getId()).get();
        assertAll(
                () -> assertThat(memberProfileImageResponse.profileImageUrl()).isEqualTo(imageUrl),
                () -> assertThat(result.getImageUrl()).isEqualTo(imageUrl)
        );
    }

    @DisplayName("프로필 이미지를 수정하려는 사용자의 정보를 찾을 수 없다.")
    @Test
    void cannotChangeUnknownProfileImage() {
        // given
        Member member = memberRepository.save(MemberFixture.create("staccato"));
        memberRepository.delete(member);
        String imageUrl = "image.jpg";

        // when
        assertThatThrownBy(() -> memberService.changeProfileImage(member, imageUrl))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 사용자 정보를 찾을 수 없어요.");
    }
}
