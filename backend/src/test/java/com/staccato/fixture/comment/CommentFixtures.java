package com.staccato.fixture.comment;

import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public class CommentFixtures {

    public static CommentBuilder defaultComment() {
        return new CommentBuilder()
                .withContent("commentContent");
    }

    public static class CommentBuilder {
        String content;
        Staccato staccato;
        Member member;

        public CommentBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public CommentBuilder withStaccato(Staccato staccato) {
            this.staccato = staccato;
            return this;
        }

        public CommentBuilder withMember(Member member) {
            this.member = member;
            return this;
        }

        public Comment build() {
            return new Comment(content, staccato, member);
        }

        public Comment buildAndSave(CommentRepository repository) {
            Comment comment = build();
            return repository.save(comment);
        }
    }
}
