package com.staccato.member.repository;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;

    private Member host;
    private Member guest;
    private Category category;

    @BeforeEach
    void init() {
        host = MemberFixtures.defaultMember()
                .withNickname("스타")
                .buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember()
                .withNickname("스타카토")
                .buildAndSave(memberRepository);
        category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
    }

    @DisplayName("주어진 문자열을 포함하는 닉네임으로 사용자 목록을 반환 시 본인은 제외한다.")
    @Test
    void readMembersByNicknameWithoutMember() {
        // when
        List<Member> result = memberRepository
                .findByNicknameNicknameContainsAndMemberIddNotAndCategoryNot(
                        "스타", host.getId(), null, InvitationStatus.REQUESTED
                );

        // then
        assertThat(result)
                .hasSize(1)
                .containsExactly(guest)
                .doesNotContain(host);
    }

    @DisplayName("주어진 문자열을 포함하는 닉네임으로 사용자 목록을 반환 시 주어진 카테고리에 속한 사용자는 제외한다.")
    @Test
    void readMembersByNicknameWithoutCategoryMember() {
        // given
        category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);
        String keyword = guest.getNickname().getNickname();
        Member searchedMember = MemberFixtures.defaultMember()
                .withNickname("다른" + keyword)
                .buildAndSave(memberRepository);

        // when
        List<Member> result = memberRepository.findByNicknameNicknameContainsAndMemberIddNotAndCategoryNot(
                keyword, host.getId(), category.getId(), InvitationStatus.REQUESTED
        );

        // then
        assertThat(result).hasSize(1)
                .containsExactly(searchedMember);
    }

    @DisplayName("주어진 문자열을 포함하는 닉네임으로 사용자 목록을 반환 시 주어진 카테고리에 초대 요청을 받은 사용자는 제외한다.")
    @Test
    void readMembersByNicknameWithoutCategoryInvitation() {
        // given
        categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest));
        String keyword = guest.getNickname().getNickname();
        Member searchedMember = MemberFixtures.defaultMember()
                .withNickname("다른" + keyword)
                .buildAndSave(memberRepository);

        // when
        List<Member> result = memberRepository
                .findByNicknameNicknameContainsAndMemberIddNotAndCategoryNot(
                        keyword, host.getId(), category.getId(), InvitationStatus.REQUESTED
                );

        // then
        assertThat(result).hasSize(1)
                .containsExactly(searchedMember);
    }

    @DisplayName("대소문자를 구분하여 닉네임 검색이 가능하다.")
    @Test
    void readMembersByNicknameIgnoreCase() {
        // given
        Member upper = MemberFixtures.defaultMember()
                .withNickname("STACCATO")
                .buildAndSave(memberRepository);
        Member lower = MemberFixtures.defaultMember()
                .withNickname("staccato")
                .buildAndSave(memberRepository);
        String keyword = "staccato";

        // when
        List<Member> result = memberRepository
                .findByNicknameNicknameContainsAndMemberIddNotAndCategoryNot(
                        keyword, host.getId(), null, InvitationStatus.REQUESTED
                );

        // then
        assertThat(result)
                .extracting(Member::getId)
                .containsExactlyInAnyOrder(lower.getId())
                .doesNotContain(upper.getId());
    }

    @DisplayName("categoryId가 null이면 카테고리 관련 필터 없이 검색된다.")
    @Test
    void readMembersByNicknameWithNullCategoryId() {
        // given
        categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest));
        String keyword = guest.getNickname().getNickname();

        // when
        List<Member> result = memberRepository
                .findByNicknameNicknameContainsAndMemberIddNotAndCategoryNot(
                        keyword, host.getId(), null, InvitationStatus.REQUESTED
                );

        // then
        assertThat(result)
                .hasSize(1)
                .containsExactly(guest);
    }
}
