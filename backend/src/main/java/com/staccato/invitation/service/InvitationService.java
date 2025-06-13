package com.staccato.invitation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.domain.InvitationStatus;
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
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final CategoryInvitationRepository categoryInvitationRepository;
    private final CategoryMemberRepository categoryMemberRepository;

    @Transactional
    public CategoryInvitationCreateResponses invite(Member inviter, CategoryInvitationRequest categoryInvitationRequest) {
        Category category = getCategoryById(categoryInvitationRequest.categoryId());
        category.validateOwner(inviter);
        category.validateHost(inviter);
        List<Member> invitees = memberRepository.findAllByIdIn(categoryInvitationRequest.inviteeIds());
        List<CategoryInvitation> invitations = categoryInvitationRepository.saveAll(createInvitations(category, inviter, invitees));

        return CategoryInvitationCreateResponses.from(invitations);
    }

    private Category getCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 카테고리를 찾을 수 없어요."));
    }

    private List<CategoryInvitation> createInvitations(Category category, Member inviter, List<Member> invitees) {
        List<CategoryInvitation> categoryInvitations = new ArrayList<>();

        for (Member invitee : invitees) {
            validateIfAlreadyCategoryMember(category, invitee);
            validateIfAlreadyRequested(category, inviter, invitee);
            CategoryInvitation categoryInvitation = CategoryInvitation.invite(category, inviter, invitee);
            categoryInvitations.add(categoryInvitation);
        }

        return categoryInvitations;
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

    public CategoryInvitationSentResponses readSentInvitations(Member inviter) {
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllRequestedWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(inviter.getId());
        return CategoryInvitationSentResponses.from(invitations);
    }

    @Transactional
    public void cancel(Member inviter, long invitationId) {
        CategoryInvitation invitation = getCategoryInvitationById(invitationId);
        validateInviter(invitation, inviter);
        invitation.cancel();
    }

    private void validateInviter(CategoryInvitation invitation, Member inviter) {
        if (invitation.isNotInviter(inviter)) {
            throw new ForbiddenException();
        }
    }

    @Transactional
    public void accept(Member invitee, long invitationId) {
        CategoryInvitation invitation = getCategoryInvitationById(invitationId);
        invitation.validateInvitee(invitee);
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
        CategoryInvitation invitation = getCategoryInvitationById(invitationId);
        invitation.validateInvitee(invitee);
        invitation.reject();
    }

    private CategoryInvitation getCategoryInvitationById(long invitationId) {
        return categoryInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new StaccatoException("요청하신 초대 정보를 찾을 수 없어요."));
    }

    public CategoryInvitationReceivedResponses readReceivedInvitations(Member invitee) {
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllRequestedWithCategoryAndInviterByInviteeIdOrderByCreatedAtDesc(invitee.getId());
        return CategoryInvitationReceivedResponses.from(invitations);
    }
}
