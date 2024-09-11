package com.staccato.fixture.comment;

import java.util.Objects;

public class CommentUpdateRequestFixture {
    public static String create(String content){
        if (Objects.isNull(content)) {
            return "{}";
        }
        return "{"
                + "\"content\": \"" + content + "\""
                + "}";
    }
}
