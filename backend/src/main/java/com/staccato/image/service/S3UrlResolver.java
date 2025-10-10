package com.staccato.image.service;

import java.net.URI;

public final class S3UrlResolver {
    private final String bucketName;
    private final String endPoint;
    private final String cloudFrontEndPoint;

    public S3UrlResolver(String bucketName, String endPoint, String cloudFrontEndPoint) {
        this.bucketName = bucketName;
        this.endPoint = nullToEmpty(endPoint);
        this.cloudFrontEndPoint = nullToEmpty(cloudFrontEndPoint);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public String toPublicUrl(String key) {
        String base = firstNonBlank(cloudFrontEndPoint, endPoint, "http://fake-s3");
        return join(base, key);
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) {
            if (hasText(v)) {
                return v;
            }
        }
        return "";
    }

    private static String join(String base, String key) {
        String b = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String k = key.startsWith("/") ? key.substring(1) : key;
        return b + "/" + k;
    }

    public String extractKey(String urlOrPath) {
        String result = urlOrPath;

        if (hasText(cloudFrontEndPoint)) {
            result = stripPrefix(result, normalize(cloudFrontEndPoint));
        }
        if (hasText(endPoint)) {
            result = stripPrefix(result, normalize(endPoint));
        }

        if (looksLikeUrl(result)) {
            try {
                String path = URI.create(result).getPath();
                result = path != null ? path : result;
            } catch (IllegalArgumentException ignore) {
            }
        }

        result = result.replaceFirst("^/+", "");

        String bucketPrefix = bucketName + "/";
        if (hasText(bucketName) && result.startsWith(bucketPrefix)) {
            result = result.substring(bucketPrefix.length());
        }

        return result;
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private static String normalize(String base) {
        return base.endsWith("/") ? base : base + "/";
    }

    private static String stripPrefix(String text, String prefix) {
        return text.startsWith(prefix) ? text.substring(prefix.length()) : text;
    }

    private static boolean looksLikeUrl(String s) {
        return s.startsWith("http://") || s.startsWith("https://");
    }
}