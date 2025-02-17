package com.staccato.config.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.staccato.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenProperties.class)
public abstract class AbstractTokenProvider {
    protected final TokenProperties tokenProperties;

    public abstract String create(Object payload);

    public Claims getPayload(String token) {
        try {
            byte[] key = tokenProperties.secretKey().getBytes();
            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException();
        }
    }
}
