package com.staccato.staccato.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.category.service.CategoryValidator;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.jwt.ShareTokenProvider;
import com.staccato.config.jwt.dto.ShareTokenPayload;
import com.staccato.config.log.annotation.Trace;
import com.staccato.member.domain.Member;
import com.staccato.member.service.MemberValidator;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.service.dto.response.StaccatoShareLinkResponse;
import com.staccato.staccato.service.dto.response.StaccatoSharedResponse;

import lombok.RequiredArgsConstructor;

@Trace
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StaccatoShareService {

    @Value("${staccato.share.link-prefix}")
    private String shareLinkPrefix;

    private final ShareTokenProvider shareTokenProvider;
    private final CommentRepository commentRepository;
    private final StaccatoValidator staccatoValidator;
    private final MemberValidator memberValidator;

    public StaccatoShareLinkResponse createStaccatoShareLink(Long staccatoId, Member member) {
        Staccato staccato = staccatoValidator.getStaccatoByIdOrThrow(staccatoId);
        staccato.validateReadPermission(member);

        ShareTokenPayload shareTokenPayload = new ShareTokenPayload(staccatoId, member.getId());
        String token = shareTokenProvider.create(shareTokenPayload);
        String shareLink = shareLinkPrefix + token;

        return new StaccatoShareLinkResponse(staccatoId, shareLink, token);
    }

    public StaccatoSharedResponse readSharedStaccatoByToken(String token) {
        shareTokenProvider.validateToken(token);
        long staccatoId = shareTokenProvider.extractStaccatoId(token);
        long memberId = shareTokenProvider.extractMemberId(token);
        LocalDateTime expiredAt = shareTokenProvider.extractExpiredAt(token);

        Staccato staccato = staccatoValidator.getStaccatoByIdOrThrow(staccatoId);
        Member member = memberValidator.getMemberByIdOrThrow(memberId);
        List<Comment> comments = commentRepository.findAllByStaccatoId(staccatoId);

        return new StaccatoSharedResponse(expiredAt, staccato, member, comments);
    }
}
