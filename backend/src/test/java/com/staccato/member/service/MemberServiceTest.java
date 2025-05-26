package com.staccato.member.service;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.member.MemberReadRequestFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.member.service.dto.request.MemberReadRequest;
import com.staccato.member.service.dto.response.MemberProfileImageResponse;
import com.staccato.member.service.dto.response.MemberResponse;
import com.staccato.member.service.dto.response.MemberResponses;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


class MemberServiceTest extends ServiceSliceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryInvitationRepository categoryInvitationRepository;

    @DisplayName("주어진 이미지 경로로 사용자의 프로필 사진을 변경한다.")
    @Test
    void changeProfileImage() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        memberRepository.delete(member);
        String imageUrl = "image.jpg";

        // when
        assertThatThrownBy(() -> memberService.changeProfileImage(member, imageUrl))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 사용자 정보를 찾을 수 없어요.");
    }

    @DisplayName("주어진 문자열을 포함하는 닉네임을 가진 사용자 목록을 반환한다.")
    @Test
    void readMembersByNickname() {
        // given
        Member member = MemberFixtures.defaultMember()
                .withNickname("사용자")
                .buildAndSave(memberRepository);
        Member member1 = MemberFixtures.defaultMember()
                .withNickname("스타카토")
                .buildAndSave(memberRepository);
        Member member2 = MemberFixtures.defaultMember()
                .withNickname("스타")
                .buildAndSave(memberRepository);
        Member member3 = MemberFixtures.defaultMember()
                .withNickname("타스")
                .buildAndSave(memberRepository);
        String keyword = "스타";
        MemberReadRequest memberReadRequest = MemberReadRequestFixtures.defaultMemberReadRequest()
                .withNickname(keyword)
                .build();

        // when
        MemberResponses result = memberService.readMembersByNickname(member, memberReadRequest);

        // then
        List<Long> resultIds = result.members().stream()
                .map(MemberResponse::memberId)
                .toList();

        assertThat(resultIds)
                .hasSize(2)
                .containsExactlyInAnyOrder(member1.getId(), member2.getId())
                .doesNotContain(member.getId(), member3.getId());
    }

    @DisplayName("검색어가 없다면, 빈 배열을 반환한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void readMembersByBlankNickname(String keyword) {
        // given
        Member member = MemberFixtures.defaultMember()
                .withNickname("스타")
                .buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(member)
                .buildAndSave(categoryRepository);
        MemberReadRequest memberReadRequest = MemberReadRequestFixtures.defaultMemberReadRequest()
                .withNickname(keyword)
                .withExcludeCategoryId(category.getId())
                .build();

        // when
        MemberResponses result = memberService.readMembersByNickname(member, memberReadRequest);

        // then
        assertThat(result.members()).isEmpty();
    }
}
