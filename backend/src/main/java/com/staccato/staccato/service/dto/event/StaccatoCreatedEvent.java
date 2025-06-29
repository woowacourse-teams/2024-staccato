package com.staccato.staccato.service.dto.event;

import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public record StaccatoCreatedEvent(Member creator, Category category, Staccato staccato) {
}
