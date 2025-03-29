package com.staccato.staccato.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaccatoShareLinkFactory {

    private static String shareLinkPrefix;

    public StaccatoShareLinkFactory(@Value("${staccato.share.link-prefix}") String linkPrefix) {
        shareLinkPrefix = linkPrefix;
    }

    public static String create(String token) {
        return shareLinkPrefix + token;
    }

    public static String extractToken(String shareLink) {
        return shareLink.substring(shareLinkPrefix.length());
    }
}
