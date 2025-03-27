package com.staccato.util;

import java.util.Date;

import com.staccato.config.jwt.AbstractTokenProvider;
import com.staccato.config.jwt.TokenProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class StubTokenProvider extends AbstractTokenProvider {
    private static final String TEST_PROPERTY_NAME = "data";
    private static final String TEST_SECRET_KEY = "test-secret-key";

    private StubTokenProvider(TokenProperties tokenProperties) {
        super(tokenProperties);
    }

    public static StubTokenProvider withTestKey() {
        return new StubTokenProvider(new TokenProperties(TEST_SECRET_KEY));
    }

    @Override
    public String create(Object payload) {
        return Jwts.builder()
                .claim(TEST_PROPERTY_NAME, payload)
                .signWith(SignatureAlgorithm.HS256, tokenProperties.secretKey().getBytes())
                .compact();
    }

    public String createExpired(Object payload) {
        return Jwts.builder()
                .claim(TEST_PROPERTY_NAME, payload)
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS256, tokenProperties.secretKey().getBytes())
                .compact();
    }
}
