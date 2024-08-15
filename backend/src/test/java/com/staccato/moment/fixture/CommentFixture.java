package com.staccato.moment.fixture;

import com.staccato.member.domain.Member;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.Comment;

public class CommentFixture {
    public static Comment create(Moment moment, Member member) {
        return Comment.builder()
                .content("Sample Moment Log")
                .moment(moment)
                .member(member)
                .build();
    }
}
