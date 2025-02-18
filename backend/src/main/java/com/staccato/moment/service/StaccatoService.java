package com.staccato.moment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.config.jwt.ShareTokenProvider;
import com.staccato.config.log.annotation.Trace;

import lombok.RequiredArgsConstructor;

@Trace
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StaccatoService {
    private static final String SHARE_LINK_PREFIX = "https://staccato.kr/staccatos?token=";

    private final ShareTokenProvider shareTokenProvider;

    public String createStaccatoShareLink(Long staccatoId) {
        String token = shareTokenProvider.create(staccatoId);

        return SHARE_LINK_PREFIX + token;
    }
}
