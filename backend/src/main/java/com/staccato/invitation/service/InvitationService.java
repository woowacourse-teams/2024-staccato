package com.staccato.invitation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.category.service.CategoryValidator;
import com.staccato.config.log.annotation.Trace;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.CategoryInvitationCreateResponses;
import com.staccato.invitation.service.dto.response.CategoryInvitationReceivedResponses;
import com.staccato.invitation.service.dto.response.CategoryInvitationSentResponses;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {

    private final MemberRepository memberRepository;
    private final CategoryInvitationRepository categoryInvitationRepository;
    private final CategoryMemberRepository categoryMemberRepository;
    private final CategoryValidator categoryValidator;
    private final CategoryInvitationValidator categoryInvitationValidator;

    @Transactional
    public CategoryInvitationCreateResponses invite(Member inviter, CategoryInvitationRequest categoryInvitationRequest) {
        Category category = categoryValidator.getCategoryByIdOrThrow(categoryInvitationRequest.categoryId());
        category.validateModifyPermission(inviter);
        List<Member> invitees = memberRepository.findAllByIdIn(categoryInvitationRequest.inviteeIds());
        List<CategoryInvitation> invitations = categoryInvitationRepository.saveAll(createInvitations(category, inviter, invitees));

        return CategoryInvitationCreateResponses.from(invitations);
    }

    private List<CategoryInvitation> createInvitations(Category category, Member inviter, List<Member> invitees) {
        List<CategoryInvitation> categoryInvitations = new ArrayList<>();

        for (Member invitee : invitees) {
            categoryValidator.validateNotCategoryMember(category, invitee);
            categoryInvitationValidator.validateNotAlreadyRequested(category, inviter, invitee);
            CategoryInvitation categoryInvitation = CategoryInvitation.invite(category, inviter, invitee);
            categoryInvitations.add(categoryInvitation);
        }

        return categoryInvitations;
    }

    public CategoryInvitationSentResponses readSentInvitations(Member inviter) {
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllRequestedWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(inviter.getId());
        return CategoryInvitationSentResponses.from(invitations);
    }

    @Transactional
    public void cancel(Member inviter, long invitationId) {
        CategoryInvitation invitation = categoryInvitationValidator.getCategoryInvitationByIdOrThrow(invitationId);
        categoryInvitationValidator.validateInviter(invitation, inviter);
        invitation.cancel();
    }

    @Transactional
    public void accept(Member invitee, long invitationId) {
        CategoryInvitation invitation = categoryInvitationValidator.getCategoryInvitationByIdOrThrow(invitationId);
        categoryInvitationValidator.validateInvitee(invitation, invitee);
        invitation.accept();

        Category category = invitation.getCategory();
        if (isInviteeNotInCategory(invitee, category)) {
            category.addGuests(List.of(invitation.getInvitee()));
        }
    }

    private boolean isInviteeNotInCategory(Member invitee, Category category) {
        return !categoryMemberRepository.existsByCategoryIdAndMemberId(category.getId(), invitee.getId());
    }

    @Transactional
    public void reject(Member invitee, long invitationId) {
        CategoryInvitation invitation = categoryInvitationValidator.getCategoryInvitationByIdOrThrow(invitationId);
        categoryInvitationValidator.validateInvitee(invitation, invitee);
        invitation.reject();
    }

    public CategoryInvitationReceivedResponses readReceivedInvitations(Member invitee) {
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllRequestedWithCategoryAndInviterByInviteeIdOrderByCreatedAtDesc(invitee.getId());
        return CategoryInvitationReceivedResponses.from(invitations);
    }
}
