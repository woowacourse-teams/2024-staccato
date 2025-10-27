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
    private static final String STATUS_EXCEPTION_MESSAGE = "이미 처리된 초대 요청이에요.";

    private Member host;
    private Member guest;
    private Category category;

    @BeforeEach
    void init() {
        host = MemberFixtures.ofDefault().withNickname("host").build();
        guest = MemberFixtures.ofDefault().withNickname("guest").build();
        category = CategoryFixtures.ofDefault().withHost(host).build();
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
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.cancel();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.CANCELED);
    }

    @DisplayName("이미 취소된 초대를 취소해도 아무 일도 일어나지 않는다.")
    @Test
    void cancelInvitationAlreadyCanceled() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.cancel();

        // then
        assertThatNoException().isThrownBy(invitation::cancel);
    }

    @DisplayName("이미 수락된 초대를 취소하려고 하면 예외가 발생한다.")
    @Test
    void cannotCancelIfAccepted() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.accept();

        // then
        assertThatThrownBy(invitation::cancel)
                .isInstanceOf(StaccatoException.class)
                .hasMessage(STATUS_EXCEPTION_MESSAGE);
    }

    @DisplayName("이미 거절된 초대를 취소하려고 하면 예외가 발생한다.")
    @Test
    void cannotCancelIfRejected() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.reject();

        // then
        assertThatThrownBy(invitation::cancel)
                .isInstanceOf(StaccatoException.class)
                .hasMessage(STATUS_EXCEPTION_MESSAGE);
    }

    @DisplayName("초대를 수락한다.")
    @Test
    void accept() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.accept();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
    }

    @DisplayName("이미 수락된 초대를 수락해도 아무 일도 일어나지 않는다.")
    @Test
    void acceptInvitationAlreadyAccepted() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.accept();

        // then
        assertThatNoException().isThrownBy(invitation::accept);
    }

    @DisplayName("이미 취소된 초대를 수락하려고 하면 예외가 발생한다.")
    @Test
    void cannotAcceptIfCanceled() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.cancel();

        // then
        assertThatThrownBy(invitation::accept)
                .isInstanceOf(StaccatoException.class)
                .hasMessage(STATUS_EXCEPTION_MESSAGE);
    }

    @DisplayName("이미 거절된 초대를 수락하려고 하면 예외가 발생한다.")
    @Test
    void cannotAcceptIfRejected() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.reject();

        // then
        assertThatThrownBy(invitation::accept)
                .isInstanceOf(StaccatoException.class)
                .hasMessage(STATUS_EXCEPTION_MESSAGE);
    }

    @DisplayName("초대를 거절한다.")
    @Test
    void reject() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.reject();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.REJECTED);
    }

    @DisplayName("이미 거절된 초대를 거절해도 아무 일도 일어나지 않는다.")
    @Test
    void rejectInvitationAlreadyRejected() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.reject();

        // then
        assertThatNoException().isThrownBy(invitation::reject);
    }

    @DisplayName("이미 취소된 초대를 거절하려고 하면 예외가 발생한다.")
    @Test
    void cannotRejectIfCanceled() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.cancel();

        // then
        assertThatThrownBy(invitation::reject)
                .isInstanceOf(StaccatoException.class)
                .hasMessage(STATUS_EXCEPTION_MESSAGE);
    }

    @DisplayName("이미 수락된 초대를 거절하려고 하면 예외가 발생한다.")
    @Test
    void cannotRejectIfAccepted() {
        // given
        CategoryInvitation invitation = CategoryInvitation.invite(category, host, guest);

        // when
        invitation.accept();

        // then
        assertThatThrownBy(invitation::reject)
                .isInstanceOf(StaccatoException.class)
                .hasMessage(STATUS_EXCEPTION_MESSAGE);
    }
}
