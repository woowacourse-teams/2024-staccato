package com.staccato.invitation.service;

import org.springframework.stereotype.Component;

import com.staccato.category.domain.Category;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryInvitationValidator {

    private final CategoryInvitationRepository categoryInvitationRepository;

    public CategoryInvitation getCategoryInvitationByIdOrThrow(long invitationId) {
        return categoryInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new StaccatoException("요청하신 초대 정보를 찾을 수 없어요."));
    }

    public void validateNotAlreadyRequested(Category category, Member inviter, Member invitee) {
        if (categoryInvitationRepository.existsByCategoryIdAndInviterIdAndInviteeIdAndStatus(
                category.getId(), inviter.getId(), invitee.getId(), InvitationStatus.REQUESTED)) {
            throw new StaccatoException("이미 초대 요청을 보낸 사용자입니다.");
        }
    }

    public void validateInviter(CategoryInvitation invitation, Member inviter) {
        if (invitation.isNotInviter(inviter)) {
            throw new ForbiddenException();
        }
    }

    public void validateInvitee(CategoryInvitation invitation, Member invitee) {
        if (invitation.isNotInvitee(invitee)) {
            throw new ForbiddenException();
        }
    }
}
