package com.staccato.staccato.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.jwt.ShareTokenProvider;
import com.staccato.config.jwt.dto.ShareTokenPayload;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;
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
    private final StaccatoRepository staccatoRepository;
    private final MemberRepository memberRepository;

    public StaccatoShareLinkResponse createStaccatoShareLink(Long staccatoId, Member member) {
        Staccato staccato = getStaccatoById(staccatoId);
        staccato.validateOwner(member);

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

        Staccato staccato = getStaccatoById(staccatoId);
        Member member = getMemberById(memberId);
        List<Comment> comments = commentRepository.findAllByStaccatoId(staccatoId);

        return new StaccatoSharedResponse(expiredAt, staccato, member, comments);
    }

    private Staccato getStaccatoById(long staccatoId) {
        return staccatoRepository.findById(staccatoId)
                .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new StaccatoException("요청하신 멤버를 찾을 수 없어요."));
    }
}
