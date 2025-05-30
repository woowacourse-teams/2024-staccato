package com.staccato.comment.service.dto.event;

import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public record CommentCreatedEvent(Member commenter, Category category, Staccato staccato) {
}
