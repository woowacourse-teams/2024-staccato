package com.staccato.config.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.staccato.config.jwt.dto.ShareTokenPayload;
import com.staccato.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class ShareTokenProvider extends AbstractTokenProvider {
    private static final long EXPIRATION_TIME_15DAYS = 1000 * 60 * 60 * 24 * 15;

    public ShareTokenProvider(TokenProperties tokenProperties) {
        super(tokenProperties);
    }

    @Override
    public String create(Object payload) {
        ShareTokenPayload tokenPayload = (ShareTokenPayload) payload;

        return Jwts.builder()
                .claim("staccatoId", tokenPayload.staccatoId())
                .claim("memberId", tokenPayload.memberId())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_15DAYS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public long extractStaccatoId(String token) {
        Claims claims = getPayload(token);
        return claims.get("staccatoId", Long.class);
    }

    public long extractMemberId(String token) {
        Claims claims = getPayload(token);
        return claims.get("memberId", Long.class);
    }

    public LocalDateTime extractExpiredAt(String token) {
        try {
            Claims claims = getPayload(token);
            Date expiredAt = claims.getExpiration();
            return expiredAt.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException();
        }
    }
}
