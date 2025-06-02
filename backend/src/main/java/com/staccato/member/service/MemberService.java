package com.staccato.member.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.member.service.dto.request.MemberReadRequest;
import com.staccato.member.service.dto.response.MemberProfileImageResponse;
import com.staccato.member.service.dto.response.MemberSearchResponse;
import com.staccato.member.service.dto.response.MemberSearchResponses;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final CategoryMemberRepository categoryMemberRepository;
    private final CategoryInvitationRepository categoryInvitationRepository;
    private final MemberValidator memberValidator;

    @Transactional
    public MemberProfileImageResponse changeProfileImage(Member member, String imageUrl) {
        Member target = memberValidator.getMemberByIdOrThrow(member.getId());
        target.updateImage(imageUrl);
        return new MemberProfileImageResponse(target.getImageUrl());
    }

    public MemberSearchResponses readMembersByNickname(Member member, MemberReadRequest memberReadRequest) {
        if (hasNoNickname(memberReadRequest.nickname())) {
            return MemberSearchResponses.empty();
        }
        List<Member> members = memberRepository.findByNicknameNicknameContainsAndIdNot(memberReadRequest.trimmedNickname(), member.getId());

        return getMemberSearchResponses(memberReadRequest, members);
    }

    private MemberSearchResponses getMemberSearchResponses(MemberReadRequest memberReadRequest, List<Member> members) {
        if (hasNoCategoryToExclude(memberReadRequest.excludeCategoryId())) {
            return MemberSearchResponses.none(members);
        }
        Set<Long> categoryMemberIds = categoryMemberRepository.findAllByCategoryIdAndMemberIn(memberReadRequest.excludeCategoryId(), members)
                .stream().map(categoryMember -> categoryMember.getMember().getId()).collect(Collectors.toSet());
        Set<Long> inviteeIds = categoryInvitationRepository.findAllByCategoryIdAndInviteeInAndStatus(
                        memberReadRequest.excludeCategoryId(), members, InvitationStatus.REQUESTED)
                .stream().map(categoryInvitation -> categoryInvitation.getInvitee().getId())
                .collect(Collectors.toSet());
        List<MemberSearchResponse> responses = members.stream()
                .map(m -> resolveSearchStatus(m, categoryMemberIds, inviteeIds))
                .toList();
        return new MemberSearchResponses(responses);
    }

    private boolean hasNoNickname(String nickname) {
        return Objects.isNull(nickname) || nickname.isBlank();
    }

    private boolean hasNoCategoryToExclude(long categoryId) {
        return categoryId <= 0;
    }

    private MemberSearchResponse resolveSearchStatus(Member m, Set<Long> categoryMemberIds, Set<Long> inviteeIds) {
        if (categoryMemberIds.contains(m.getId())) {
            return MemberSearchResponse.joined(m);
        } else if (inviteeIds.contains(m.getId())) {
            return MemberSearchResponse.requested(m);
        }
        return MemberSearchResponse.none(m);
    }
}
