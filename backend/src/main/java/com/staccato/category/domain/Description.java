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
public class Description {
    private static final int MAX_LENGTH = 500;

    @Column(columnDefinition = "TEXT", length = MAX_LENGTH)
    private String description;

    public Description(String description) {
        validateLength(description);
        this.description = description;
    }

    private void validateLength(String description) {
        if(description.length()>MAX_LENGTH){
            throw new StaccatoException("내용의 최대 허용 글자수는 공백 포함 500자입니다.");
        }
    }
}
