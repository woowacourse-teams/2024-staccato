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

        result = removeBaseEndpoints(result);
        result = stripQueryAndFragment(result);
        result = extractPathIfUrl(result);
        result = result.replaceFirst("^/+", "");
        result = removeBucketPrefix(result);

        return result;
    }

    private String removeBaseEndpoints(String s) {
        String result = s;
        if (hasText(cloudFrontEndPoint)) {
            result = stripPrefix(result, normalize(cloudFrontEndPoint));
        }
        if (hasText(endPoint)) {
            result = stripPrefix(result, normalize(endPoint));
        }
        return result;
    }

    private String stripQueryAndFragment(String s) {
        int queryIdx = s.indexOf('?');
        int fragmentIdx = s.indexOf('#');
        int cutIdx = (queryIdx == -1) ? fragmentIdx
                : (fragmentIdx == -1 ? queryIdx : Math.min(queryIdx, fragmentIdx));
        return (cutIdx != -1) ? s.substring(0, cutIdx) : s;
    }

    private String extractPathIfUrl(String s) {
        if (looksLikeUrl(s)) {
            try {
                String path = URI.create(s).getPath();
                return path != null ? path : s;
            } catch (IllegalArgumentException ignore) {
            }
        }
        return s;
    }

    private String removeBucketPrefix(String s) {
        String bucketPrefix = bucketName + "/";
        if (hasText(bucketName) && s.startsWith(bucketPrefix)) {
            return s.substring(bucketPrefix.length());
        }
        return s;
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
