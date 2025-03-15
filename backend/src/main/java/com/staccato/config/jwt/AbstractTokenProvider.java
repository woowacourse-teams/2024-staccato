package com.staccato.config.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.staccato.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
@EnableConfigurationProperties(TokenProperties.class)
public abstract class AbstractTokenProvider {
    protected final TokenProperties tokenProperties;
    protected final Key secretKey;

    public AbstractTokenProvider(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
        this.secretKey = new SecretKeySpec(
                tokenProperties.secretKey().getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName()
        );
    }

    public abstract String create(Object payload);

    public Claims getPayload(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey.getEncoded())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException();
        }
    }

    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenProperties.secretKey().getBytes())
                    .parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException();
        }
    }
}
