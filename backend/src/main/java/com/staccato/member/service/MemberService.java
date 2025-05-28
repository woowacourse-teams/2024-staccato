package com.staccato.member.service;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.exception.StaccatoException;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.member.service.dto.request.MemberReadRequest;
import com.staccato.member.service.dto.response.MemberProfileImageResponse;
import com.staccato.member.service.dto.response.MemberResponses;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberProfileImageResponse changeProfileImage(Member member, String imageUrl) {
        Member target = getMemberById(member.getId());
        target.updateImage(imageUrl);
        return new MemberProfileImageResponse(target.getImageUrl());
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new StaccatoException("요청하신 사용자 정보를 찾을 수 없어요."));
    }

    public MemberResponses readMembersByNickname(Member member, MemberReadRequest memberReadRequest) {
        if (hasNoNickname(memberReadRequest.nickname())) {
            return MemberResponses.empty();
        }
        List<Member> members = memberRepository.findByNicknameContainsWithoutMemberIdAndCategoryId(
                memberReadRequest.trimmedNickname(),
                member.getId(),
                memberReadRequest.excludeCategoryId(),
                InvitationStatus.REQUESTED);
        return MemberResponses.of(members);
    }

    private boolean hasNoNickname(String nickname) {
        return Objects.isNull(nickname) || nickname.isBlank();
    }
}
