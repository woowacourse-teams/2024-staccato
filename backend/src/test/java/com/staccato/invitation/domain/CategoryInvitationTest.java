package com.staccato.invitation.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.category.domain.Category;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryInvitationTest {
    private Member host;
    private Member guest;
    private Category category;

    @BeforeEach
    void init() {
        host = MemberFixtures.defaultMember().withNickname("host").build();
        guest = MemberFixtures.defaultMember().withNickname("guest").build();
        category = CategoryFixtures.defaultCategory().withHost(host).build();
    }

    @DisplayName("주어진 카테고리, 초대자, 초대 대상에 대한 초대 내역(REQUESTED)를 생성한다.")
    @Test
    void invite() {
        // when
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // then
        assertAll(
                () -> assertThat(invitation.getCategory()).isEqualTo(category),
                () -> assertThat(invitation.getInviter()).isEqualTo(host),
                () -> assertThat(invitation.getInvitee()).isEqualTo(guest),
                () -> assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.REQUESTED)
        );
    }
}
