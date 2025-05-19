package com.staccato.invitation.service;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.dto.CategoryInvitationRequest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final CategoryInvitationRepository categoryInvitationRepository;

    @Transactional
    public void inviteMembers(Member inviter, CategoryInvitationRequest categoryInvitationRequest) {
        Category category = getCategoryById(categoryInvitationRequest.categoryId());
        validateModificationPermission(category, inviter);
        Set<Long> memberIds = categoryInvitationRequest.memberIds();
        List<Member> invitedMembers = memberRepository.findAllByIdIn(memberIds);
        List<CategoryInvitation> invitations = invitedMembers.stream()
                .map(invitedMember -> CategoryInvitation.invite(category, inviter, invitedMember))
                .toList();
        categoryInvitationRepository.saveAll(invitations);
    }

    private Category getCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 카테고리를 찾을 수 없어요."));
    }

    private void validateModificationPermission(Category category, Member member) {
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

    //TODO: 초대 요청 목록 조회 API 구현
    public List<CategoryInvitation> readInvitations(Member inviter){
        return categoryInvitationRepository.findAllWithCategoryAndMembersByInviterId(inviter.getId());
    }
}
