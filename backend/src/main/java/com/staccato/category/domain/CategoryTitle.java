package com.staccato.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import com.staccato.exception.StaccatoException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@EqualsAndHashCode
public class CategoryTitle {
    private static final int COLUMN_MAX_LENGTH = 50;
    private static final int TITLE_MAX_LENGTH = 30;

    @Column(nullable = false, length = COLUMN_MAX_LENGTH)
    private String title;

    public CategoryTitle(String title) {
        String trimmedTitle = title.trim();
        validateLength(trimmedTitle);
        this.title = trimmedTitle;
    }

    private void validateLength(String trimmedTitle) {
        if (trimmedTitle.isEmpty() || trimmedTitle.length() > TITLE_MAX_LENGTH) {
            throw new StaccatoException("제목은 공백 포함 30자 이하로 설정해주세요.");
        }
    }

    public boolean isSame(CategoryTitle title) {
        return this.title.equals(title.title);
    }
}
