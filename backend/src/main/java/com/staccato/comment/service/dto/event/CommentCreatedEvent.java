package com.staccato.comment.service.dto.event;

import com.staccato.category.domain.Category;
import com.staccato.comment.domain.Comment;
import com.staccato.member.domain.Member;

public record CommentCreatedEvent(Member commenter, Category category, Comment comment) {
}
