package com.staccato.config.auth;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.staccato.member.domain.Member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenProperties.class)
public class TokenProvider {
    private final TokenProperties tokenProperties;

    public String create(Member member) {
        return Jwts.builder()
                .claim("id", member.getId())
                .claim("nickname", member.getNickname())
                .claim("createdAt", member.getCreatedAt().toString())
                .signWith(SignatureAlgorithm.HS256, tokenProperties.secretKey().getBytes())
                .compact();
    }
}
