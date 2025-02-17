package com.staccato.config.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class ShareTokenProvider extends AbstractTokenProvider {
    private final static String PROPERTY_NAME = "staccatoId";

    public ShareTokenProvider(TokenProperties tokenProperties) {
        super(tokenProperties);
    }

    @Override
    public String create(Object payload) {
        long staccatoId = (long) payload;
        return Jwts.builder()
                .claim(PROPERTY_NAME, staccatoId)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, tokenProperties.secretKey().getBytes())
                .compact();
    }

    public long extractStaccatoId(String token) {
        Claims claims = getPayload(token);
        return claims.get(PROPERTY_NAME, Long.class);
    }
}
