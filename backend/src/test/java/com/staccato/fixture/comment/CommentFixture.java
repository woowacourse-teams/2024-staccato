package com.staccato.fixture.comment;

import com.staccato.comment.domain.Comment;
import com.staccato.member.domain.Member;
import com.staccato.moment.domain.Moment;

public class CommentFixture {
    public static Comment create(Moment moment, Member member) {
        return Comment.builder()
                .content("Sample Moment Log")
                .moment(moment)
                .member(member)
                .build();
    }
}
