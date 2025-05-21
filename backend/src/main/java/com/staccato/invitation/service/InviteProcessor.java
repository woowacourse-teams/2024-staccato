package com.staccato.invitation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.exception.StaccatoException;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.dto.response.InvitationResultResponse;
import com.staccato.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteProcessor {
    private final CategoryMemberRepository categoryMemberRepository;
    private final CategoryInvitationRepository categoryInvitationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public InvitationResultResponse process(Category category, Member inviter, Member invitee) {
        try {
            validateIfAlreadyCategoryMember(category, invitee);
            validateIfAlreadyRequested(category, inviter, invitee);
            CategoryInvitation categoryInvitation = categoryInvitationRepository.save(CategoryInvitation.invite(category, inviter, invitee));
            return InvitationResultResponse.success(categoryInvitation);
        } catch (Exception e) {
            log.error("Invitation failed for categoryId({}), inviterId({}), inviteeId({}), for Reason: {}",
                    category.getId(), inviter.getId(), invitee.getId(), e.getMessage());
            return InvitationResultResponse.fail(invitee, e.getMessage());
        }
    }

    private void validateIfAlreadyCategoryMember(Category category, Member invitee) {
        if (categoryMemberRepository.existsByCategoryIdAndMemberId(category.getId(), invitee.getId())) {
            throw new StaccatoException("이미 카테고리에 함께하고 있는 사용자입니다.");
        }
    }

    private void validateIfAlreadyRequested(Category category, Member inviter, Member invitee) {
        if (categoryInvitationRepository.existsByCategoryIdAndInviterIdAndInviteeIdAndStatus(
                category.getId(), inviter.getId(), invitee.getId(), InvitationStatus.REQUESTED)) {
            throw new StaccatoException("이미 초대 요청을 보낸 사용자입니다.");
        }
    }
}
