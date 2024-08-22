package com.staccato.config.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.admin")
public record AdminProperties(String key, String token) {
}

