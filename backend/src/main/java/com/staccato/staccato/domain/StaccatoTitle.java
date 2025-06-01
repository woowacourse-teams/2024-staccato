package com.staccato.staccato.domain;

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
public class StaccatoTitle {
    private static final int MAX_LENGTH = 30;

    @Column(nullable = false)
    private String title;

    public StaccatoTitle(String title) {
        String trimmedTitle = title.trim();
        validateLength(trimmedTitle);
        this.title = trimmedTitle;
    }

    private void validateLength(String trimmedTitle) {
        if (trimmedTitle.isEmpty() || trimmedTitle.length() > MAX_LENGTH) {
            throw new StaccatoException("스타카토 제목은 공백 포함 30자 이하로 설정해주세요.");
        }
    }
}
