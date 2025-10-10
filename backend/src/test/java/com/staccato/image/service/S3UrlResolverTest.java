package com.staccato.image.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class S3UrlResolverTest {

    @Nested
    @DisplayName("toPublicUrl(key)")
    class ToPublicUrl {
        static Stream<Arguments> joinCases() {
            return Stream.of(
                    Arguments.of("http://s3.local", "k", "http://s3.local/k"),
                    Arguments.of("http://s3.local/", "k", "http://s3.local/k"),
                    Arguments.of("http://s3.local", "/k", "http://s3.local/k"),
                    Arguments.of("http://s3.local/", "/k", "http://s3.local/k")
            );
        }

        @Test
        @DisplayName("CloudFront가 있으면 그것을 사용한다")
        void usesCloudFrontWhenPresent() {
            S3UrlResolver resolver = new S3UrlResolver("my-bucket", "http://s3.local", "https://cdn.example.com");
            assertThat(resolver.toPublicUrl("images/a.png"))
                    .isEqualTo("https://cdn.example.com/images/a.png");
        }

        @Test
        @DisplayName("CloudFront가 없고 endpoint만 있으면 endpoint를 사용한다")
        void usesEndpointWhenNoCloudFront() {
            S3UrlResolver resolver = new S3UrlResolver("my-bucket", "http://s3.local", "");
            assertThat(resolver.toPublicUrl("images/a.png"))
                    .isEqualTo("http://s3.local/images/a.png");
        }

        @Test
        @DisplayName("CloudFront/endpoint 모두 없으면 기본값(http://fake-s3)을 사용한다")
        void usesFallbackWhenNone() {
            S3UrlResolver resolver = new S3UrlResolver("my-bucket", "", "");
            assertThat(resolver.toPublicUrl("images/a.png"))
                    .isEqualTo("http://fake-s3/images/a.png");
        }

        @ParameterizedTest
        @MethodSource("joinCases")
        @DisplayName("base와 key의 슬래시 유무와 상관없이 정확히 하나의 / 로 합쳐진다")
        void joinVariants(String base, String key, String expected) {
            S3UrlResolver resolver = new S3UrlResolver("b", base, "");
            assertThat(resolver.toPublicUrl(key)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("extractKey(urlOrPath)")
    class ExtractKey {

        @Test
        @DisplayName("CloudFront 전체 URL에서 key만 추출한다")
        void extractFromCloudFrontUrl() {
            S3UrlResolver resolver = new S3UrlResolver("staccato-test-bucket", "http://localhost:4566", "https://cdn.example.com");
            String url = "https://cdn.example.com/images/path/img.png";
            assertThat(resolver.extractKey(url)).isEqualTo("images/path/img.png");
        }

        @Test
        @DisplayName("S3 endpoint URL에서 key만 추출한다")
        void extractFromEndpointUrl() {
            S3UrlResolver resolver = new S3UrlResolver("staccato-test-bucket", "http://localhost:4566", "");
            String url = "http://localhost:4566/staccato-test-bucket/images/a.png";
            assertThat(resolver.extractKey(url)).isEqualTo("images/a.png");
        }

        @Test
        @DisplayName("엔드포인트/CF를 제거한 뒤에도 URL이면 URI path만 사용한다")
        void stillLooksUrlThenUsePath() {
            S3UrlResolver resolver = new S3UrlResolver("bkt", "http://s3.local", "https://cdn.test");
            String url = "https://other.example.com/bkt/dir/file.jpg";
            assertThat(resolver.extractKey(url)).isEqualTo("dir/file.jpg");
        }

        @Test
        @DisplayName("버킷 프리픽스가 있으면 제거한다")
        void removeBucketPrefixIfPresent() {
            S3UrlResolver resolver = new S3UrlResolver("staccato-test-bucket", "", "");
            assertThat(resolver.extractKey("staccato-test-bucket/images/x.png"))
                    .isEqualTo("images/x.png");
        }

        @Test
        @DisplayName("상대 경로만 들어오면 그대로 반환한다")
        void plainKeyIsReturned() {
            S3UrlResolver resolver = new S3UrlResolver("b", "", "");
            assertThat(resolver.extractKey("images/a.png")).isEqualTo("images/a.png");
        }

        @Test
        @DisplayName("선행 슬래시는 제거된다")
        void leadingSlashIsRemoved() {
            S3UrlResolver resolver = new S3UrlResolver("b", "", "");
            assertThat(resolver.extractKey("/images/a.png")).isEqualTo("images/a.png");
        }

        @ParameterizedTest
        @CsvSource({
                "https://cdn.example.com/images/a.png?x=1, images/a.png",
                "https://cdn.example.com/images/a.png#sec, images/a.png",
                "http://localhost:4566/bucket/images/a.png?download=true, images/a.png"
        })
        @DisplayName("쿼리/프래그먼트가 있어도 path만 추출한다")
        void stripQueryAndFragment(String input, String expected) {
            S3UrlResolver resolver = new S3UrlResolver("bucket", "http://localhost:4566", "https://cdn.example.com");
            assertThat(resolver.extractKey(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("엔드포인트/CF에 슬래시가 없어도 정상 처리된다 (normalize)")
        void normalizeBase() {
            S3UrlResolver resolver = new S3UrlResolver("b", "http://s3.local", "https://cdn.example.com");
            assertThat(resolver.extractKey("https://cdn.example.com/dir/a.png")).isEqualTo("dir/a.png");
            assertThat(resolver.extractKey("http://s3.local/b/dir/a.png")).isEqualTo("dir/a.png");
        }

        @Test
        @DisplayName("잘못된 URL(URI 파싱 실패)은 무시하고 원본 유지한다")
        void malformedUrlIsIgnored() {
            S3UrlResolver resolver = new S3UrlResolver("b", "http://s3.local", "https://cdn.example.com");
            String bad = "http://%/b/images/t.png";
            assertThat(resolver.extractKey(bad)).isEqualTo("http://%/b/images/t.png");
        }

        @Test
        @DisplayName("스킴 없는 도메인 문자열은 URL로 간주되지 않는다")
        void noSchemeDomainString() {
            S3UrlResolver resolver = new S3UrlResolver("b", "http://s3.local", "https://cdn.example.com");
            String s = "cdn.example.com/images/a.png";
            assertThat(resolver.extractKey(s)).isEqualTo("cdn.example.com/images/a.png");
        }

        @Test
        @DisplayName("버킷명이 비어있으면 버킷 prefix 제거 로직은 동작하지 않는다")
        void emptyBucketSkipsBucketPrefixRemoval() {
            S3UrlResolver resolver = new S3UrlResolver("", "http://s3.local", "https://cdn.example.com");
            String url = "http://s3.local/my-bucket/images/a.png";
            assertThat(resolver.extractKey(url)).isEqualTo("my-bucket/images/a.png");
        }

        @Test
        @DisplayName("엔드포인트/CF가 null이어도 안전하게 동작한다")
        void nullEndpointCloudfront() {
            S3UrlResolver resolver = new S3UrlResolver("b", null, null);
            assertThat(resolver.toPublicUrl("k")).isEqualTo("http://fake-s3/k");
            assertThat(resolver.extractKey("b/dir/a.png")).isEqualTo("dir/a.png");
        }
    }
}