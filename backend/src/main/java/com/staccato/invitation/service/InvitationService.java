package com.staccato.invitation.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.CategoryInvitationRequestedResponses;
import com.staccato.invitation.service.dto.response.InvitationResultResponse;
import com.staccato.invitation.service.dto.response.InvitationResultResponses;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final CategoryInvitationRepository categoryInvitationRepository;
    private final InviteProcessor inviteProcessor;

    public InvitationResultResponses invite(Member inviter, CategoryInvitationRequest categoryInvitationRequest) {
        Category category = getCategoryById(categoryInvitationRequest.categoryId());
        validateInvitePermission(category, inviter);
        List<Member> invitees = memberRepository.findAllByIdIn(categoryInvitationRequest.inviteeIds());
        InvitationResultResponses responses = createInvitations(category, inviter, invitees);

        return responses;
    }

    private Category getCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 카테고리를 찾을 수 없어요."));
    }

    private void validateInvitePermission(Category category, Member member) {
        validateOwner(category, member);
        validateHost(category, member);
    }

    private void validateOwner(Category category, Member member) {
        if (category.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }

    private void validateHost(Category category, Member member) {
        if (category.isGuest(member)) {
            throw new ForbiddenException();
        }
    }

    private InvitationResultResponses createInvitations(Category category, Member inviter, List<Member> invitees) {
        List<InvitationResultResponse> responses = new ArrayList<>();
        for (Member invitee : invitees) {
            responses.add(inviteProcessor.process(category, inviter, invitee));
        }
        return new InvitationResultResponses(responses);
    }

    public CategoryInvitationRequestedResponses readInvitations(Member inviter) {
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(inviter.getId());
        return CategoryInvitationRequestedResponses.from(invitations);
    }

    @Transactional
    public void cancel(Member inviter, long invitationId) {
        CategoryInvitation invitation = categoryInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new StaccatoException("요청하신 초대 정보를 찾을 수 없어요."));
        validateCancelPermission(invitation, inviter);
        invitation.cancel();
    }

    private void validateCancelPermission(CategoryInvitation invitation, Member inviter) {
        if (invitation.isNotBy(inviter)) {
            throw new ForbiddenException();
        }
    }
}
