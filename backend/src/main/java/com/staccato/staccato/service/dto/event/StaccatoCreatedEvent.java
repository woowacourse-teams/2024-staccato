package com.staccato.staccato.service.dto.event;

import com.staccato.category.domain.Category;

public record StaccatoCreatedEvent(Category category) {
}
