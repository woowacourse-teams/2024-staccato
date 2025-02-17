package com.staccato.config.jwt;

import org.springframework.stereotype.Component;

import com.staccato.member.domain.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class MemberTokenProvider extends AbstractTokenProvider {

    public MemberTokenProvider(TokenProperties tokenProperties) {
        super(tokenProperties);
    }

    public String create(Object payload) {
        Member member = (Member) payload;
        return Jwts.builder()
                .claim("id", member.getId())
                .claim("nickname", member.getNickname().getNickname())
                .claim("createdAt", member.getCreatedAt().toString())
                .signWith(SignatureAlgorithm.HS256, tokenProperties.secretKey().getBytes())
                .compact();
    }

    public long extractMemberId(String token) {
        Claims claims = getPayload(token);
        return claims.get("id", Long.class);
    }
}
