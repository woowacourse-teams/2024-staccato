package com.staccato.invitation.repository;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.category.CategoryMemberFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class CategoryInvitationRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private Member host;
    private Member guest;

    @BeforeEach
    void init() {
        host = MemberFixtures.defaultMember()
                .withNickname("host")
                .buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember()
                .withNickname("guest")
                .buildAndSave(memberRepository);
    }

    @DisplayName("특정 사용자가 요청(REQUESTED)한 초대 목록을 최신순으로 조회한다.")
    @Test
    void readAllByInviter() {
        // given
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
        Category otherCategory = CategoryFixtures.defaultCategory()
                .withHost(guest)
                .buildAndSave(categoryRepository);
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);
        CategoryInvitation invitation2 = CategoryInvitation.invite(category2, host, guest);
        CategoryInvitation otherInvitation = CategoryInvitation.invite(otherCategory, guest, host);
        categoryInvitationRepository.saveAll(List.of(invitation, invitation2, otherInvitation));

        // when
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(host.getId());

        // then
        assertThat(invitations).hasSize(2)
                .containsExactly(invitation2, invitation)
                .doesNotContain(otherInvitation);
    }

    @DisplayName("특정 사용자가 요청(REQUESTED)받은 초대 목록을 최신순으로 조회한다.")
    @Test
    void readAllByInvitee() {
        // given
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
        Category otherCategory = CategoryFixtures.defaultCategory()
                .withHost(guest)
                .buildAndSave(categoryRepository);
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);
        CategoryInvitation invitation2 = CategoryInvitation.invite(category2, host, guest);
        CategoryInvitation otherInvitation = CategoryInvitation.invite(otherCategory, guest, host);
        categoryInvitationRepository.saveAll(List.of(invitation, invitation2, otherInvitation));

        // when
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllWithCategoryAndInviterByInviteeIdOrderByCreatedAtDesc(guest.getId());

        // then
        assertThat(invitations).hasSize(2)
                .containsExactly(invitation2, invitation)
                .doesNotContain(otherInvitation);
    }

    @DisplayName("특정 카테고리의 id를 가지고 있는 모든 CategoryInvitation을 삭제한다.")
    @Test
    void deleteAllByCategoryIdInBulk() {
        // given
        Member guest2 = MemberFixtures.defaultMember().withNickname("guest2").buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest, guest2))
                .buildAndSave(categoryRepository);
        CategoryInvitation categoryInvitation1 = categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest));
        CategoryInvitation categoryInvitation2 = categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest2));

        // when
        categoryInvitationRepository.deleteAllByCategoryIdInBulk(category.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        assertAll(
                () -> assertThat(categoryInvitationRepository.existsById(categoryInvitation1.getId())).isFalse(),
                () -> assertThat(categoryInvitationRepository.existsById(categoryInvitation2.getId())).isFalse()
        );
    }
}
