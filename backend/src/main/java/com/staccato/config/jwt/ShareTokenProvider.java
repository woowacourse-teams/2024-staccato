package com.staccato.config.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.staccato.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class ShareTokenProvider extends AbstractTokenProvider {
    private static final String PROPERTY_NAME = "staccatoId";
    private static final long EXPIRATION_TIME_24H = 1000 * 60 * 60 * 24;

    public ShareTokenProvider(TokenProperties tokenProperties) {
        super(tokenProperties);
    }

    @Override
    public String create(Object payload) {
        long staccatoId = (long) payload;
        return Jwts.builder()
                .claim(PROPERTY_NAME, staccatoId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_24H))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public long extractStaccatoId(String token) {
        Claims claims = getPayload(token);
        return claims.get(PROPERTY_NAME, Long.class);
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
