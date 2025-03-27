package com.staccato.fixture.comment;

import com.staccato.comment.domain.Comment;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public class CommentFixture {
    public static Comment create(Staccato staccato, Member member) {
        return Comment.builder()
                .content("Sample Staccato Log")
                .staccato(staccato)
                .member(member)
                .build();
    }

    public static Comment create(Staccato staccato, Member member, String content) {
        return Comment.builder()
                .content(content)
                .staccato(staccato)
                .member(member)
                .build();
    }
}
