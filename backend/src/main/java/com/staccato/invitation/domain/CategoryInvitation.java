package com.staccato.invitation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.staccato.category.domain.Category;
import com.staccato.config.domain.BaseEntity;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
public class CategoryInvitation extends BaseEntity {
    private static final String STATUS_EXCEPTION_MESSAGE = "이미 처리된 초대 요청이에요.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private Member inviter;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    private Member invitee;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    public static CategoryInvitation invite(@NonNull Category category, @NonNull Member inviter, @NonNull Member invitee) {
        return new CategoryInvitation(category, inviter, invitee, InvitationStatus.REQUESTED);
    }

    public CategoryInvitation(Category category, Member inviter, Member invitee, InvitationStatus status) {
        this.category = category;
        this.inviter = inviter;
        this.invitee = invitee;
        this.status = status;
    }

    public boolean isNotInviter(Member member) {
        return !inviter.equals(member);
    }

    public void validateInvitee(Member member) {
        if (!invitee.equals(member)) {
            throw new ForbiddenException();
        }
    }

    public void cancel() {
        validateCancel(status);
        status = InvitationStatus.CANCELED;
    }

    private void validateCancel(InvitationStatus status) {
        if (status.isAccepted() || status.isRejected()) {
            throw new StaccatoException(STATUS_EXCEPTION_MESSAGE);
        }
    }

    public void accept() {
        validateAccept(status);
        status = InvitationStatus.ACCEPTED;
    }

    private void validateAccept(InvitationStatus status) {
        if (status.isCanceled() || status.isRejected()) {
            throw new StaccatoException(STATUS_EXCEPTION_MESSAGE);
        }
    }

    public void reject() {
        validateReject(status);
        status = InvitationStatus.REJECTED;
    }

    private void validateReject(InvitationStatus status) {
        if (status.isCanceled() || status.isAccepted()) {
            throw new StaccatoException(STATUS_EXCEPTION_MESSAGE);
        }
    }
}
