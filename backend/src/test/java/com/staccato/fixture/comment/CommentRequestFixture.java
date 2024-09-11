package com.staccato.fixture.comment;

import java.util.Objects;

public class CommentRequestFixture {
    public static String create(Long momentId, String content) {
        if (Objects.isNull(momentId)) {
            return "{"
                    + "\"content\": \"" + content + "\""
                    + "}";
        }
        if (Objects.isNull(content)) {
            return "{"
                    + "\"momentId\": \"" + momentId + "\""
                    + "}";
        }
        return "{"
                + "\"momentId\": \"" + momentId + "\","
                + "\"content\": \"" + content + "\""
                + "}";
    }
}
