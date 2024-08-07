package com.staccato.member.domain;

import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.staccato.exception.StaccatoException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class Nickname {
    private static final Pattern NICKNAME_REGEX = Pattern.compile("^[ㄱ-ㅎㅏ-ㅣ가-힣0-9a-zA-Z._]+$");
    private static final int MAX_LENGTH = 20;

    @Column(nullable = false, length = MAX_LENGTH)
    private String nickname;

    public Nickname(String nickname) {
        validate(nickname);
        this.nickname = nickname;
    }

    private void validate(String nickname) {
        validateLength(nickname);
        validateRegex(nickname);
    }

    private void validateLength(String nickname) {
        if (nickname.isEmpty() || nickname.length() > MAX_LENGTH) {
            throw new StaccatoException(MAX_LENGTH + "자 이내의 닉네임으로 설정해주세요.");
        }
    }

    private static void validateRegex(String nickname) {
        if (!NICKNAME_REGEX.matcher(nickname).matches()) {
            throw new StaccatoException("올바르지 않은 닉네임 형식입니다.");
        }
    }
}
