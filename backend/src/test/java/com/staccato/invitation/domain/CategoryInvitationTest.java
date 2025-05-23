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
    private CategoryInvitation invitation;

    @BeforeEach
    void init() {
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member guest = MemberFixtures.defaultMember().withNickname("guest").build();
        Category category = CategoryFixtures.defaultCategory().withHost(host).build();
        invitation = CategoryInvitation.invite(category, host, guest);
    }

    @DisplayName("주어진 카테고리, 초대자, 초대 대상에 대한 초대 내역(REQUESTED)를 생성한다.")
    @Test
    void invite() {
        // when
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member guest = MemberFixtures.defaultMember().withNickname("guest").build();
        Category category = CategoryFixtures.defaultCategory().withHost(host).build();
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
        invitation.cancel();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.CANCELED);
    }

    @DisplayName("이미 취소된 초대를 취소해도 아무 일도 일어나지 않는다.")
    @Test
    void cancelInvitationAlreadyCanceled() {
        // when
        invitation.cancel();

        // then
        assertThatNoException().isThrownBy(invitation::cancel);
    }

    @DisplayName("이미 수락된 초대를 취소하려고 하면 예외가 발생한다.")
    @Test
    void cannotCancelIfAccepted() {
        // when
        invitation.accept();

        // then
        assertThatThrownBy(invitation::cancel)
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 상대가 수락/거절한 초대 요청은 취소할 수 없어요.");
    }

    @DisplayName("이미 거절된 초대를 취소하려고 하면 예외가 발생한다.")
    @Test
    void cannotCancelIfRejected() {
        // when
        invitation.reject();

        // then
        assertThatThrownBy(invitation::cancel)
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 상대가 수락/거절한 초대 요청은 취소할 수 없어요.");
    }

    @DisplayName("초대를 수락한다.")
    @Test
    void accept() {
        // when
        invitation.accept();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
    }

    @DisplayName("이미 수락된 초대를 수락해도 아무 일도 일어나지 않는다.")
    @Test
    void acceptInvitationAlreadyAccepted() {
        // when
        invitation.accept();

        // then
        assertThatNoException().isThrownBy(invitation::accept);
    }

    @DisplayName("이미 취소된 초대를 수락하려고 하면 예외가 발생한다.")
    @Test
    void cannotAcceptIfCanceled() {
        // when
        invitation.cancel();

        // then
        assertThatThrownBy(invitation::accept)
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 상대가 취소/거절한 초대 요청은 수락할 수 없어요.");
    }

    @DisplayName("이미 거절된 초대를 수락하려고 하면 예외가 발생한다.")
    @Test
    void cannotAcceptIfRejected() {
        // when
        invitation.reject();

        // then
        assertThatThrownBy(invitation::accept)
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 상대가 취소/거절한 초대 요청은 수락할 수 없어요.");
    }

    @DisplayName("초대를 거절한다.")
    @Test
    void reject() {
        // when
        invitation.reject();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.REJECTED);
    }

    @DisplayName("이미 거절된 초대를 거절해도 아무 일도 일어나지 않는다.")
    @Test
    void rejectInvitationAlreadyRejected() {
        // when
        invitation.reject();

        // then
        assertThatNoException().isThrownBy(invitation::reject);
    }

    @DisplayName("이미 취소된 초대를 거절하려고 하면 예외가 발생한다.")
    @Test
    void cannotRejectIfCanceled() {
        // when
        invitation.cancel();

        // then
        assertThatThrownBy(invitation::reject)
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 상대가 취소/거절한 초대 요청은 거절할 수 없어요.");
    }

    @DisplayName("이미 수락된 초대를 거절하려고 하면 예외가 발생한다.")
    @Test
    void cannotRejectIfAccepted() {
        // when
        invitation.accept();

        // then
        assertThatThrownBy(invitation::reject)
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 상대가 취소/거절한 초대 요청은 거절할 수 없어요.");
    }
}
