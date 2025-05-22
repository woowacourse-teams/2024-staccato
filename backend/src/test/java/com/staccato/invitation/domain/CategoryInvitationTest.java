package com.staccato.invitation.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.category.domain.Category;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @DisplayName("초대를 취소한다.")
    @Test
    void cancel() {
        // when
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);
        invitation.cancel();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.CANCELED);
    }

    @DisplayName("이미 취소된 초대를 취소해도 아무 일도 일어나지 않는다.")
    @Test
    void cancelInvitationAlreadyCanceled() {
        // when
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);
        invitation.cancel();

        // then
        assertThatNoException().isThrownBy(() -> invitation.cancel());
    }

    @DisplayName("이미 수락된 초대를 취소하려고 하면 예외가 발생한다.")
    @Test
    void cannotCancelIfAccepted() {
        // when
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);
        invitation.accept();

        // then
        assertThatThrownBy(() -> invitation.cancel())
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 상대가 수락/거절한 초대 요청은 취소할 수 없어요.");
    }

    @DisplayName("이미 거절된 초대를 취소하려고 하면 예외가 발생한다.")
    @Test
    void cannotCancelIfRejected() {
        // when
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);
        invitation.reject();

        // then
        assertThatThrownBy(() -> invitation.cancel())
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 상대가 수락/거절한 초대 요청은 취소할 수 없어요.");
    }
}
