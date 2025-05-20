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

    public boolean isNotBy(Member inviter) {
        return !this.inviter.equals(inviter);
    }
}
