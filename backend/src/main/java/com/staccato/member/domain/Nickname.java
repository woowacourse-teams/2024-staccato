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
    private static final Pattern NICKNAME_REGEX = Pattern.compile("^(?!\\\\s+$)[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9._ ]+$");
    private static final int COLUMN_MAX_LENGTH = 20;
    private static final int NICKNAME_MAX_LENGTH = 10;

    @Column(nullable = false, length = COLUMN_MAX_LENGTH)
    private String nickname;

    public Nickname(String nickname) {
        String trimmedNickname = nickname.trim();
        validateLength(trimmedNickname);
        validateRegex(trimmedNickname);
        this.nickname = trimmedNickname;
    }

    private void validateLength(String trimmedNickname) {
        if(trimmedNickname.isEmpty() || trimmedNickname.length()>NICKNAME_MAX_LENGTH){
            throw new StaccatoException("1자 이상 10자 이하의 닉네임으로 설정해주세요.");
        }
    }

    private static void validateRegex(String nickname) {
        if (!NICKNAME_REGEX.matcher(nickname).matches()) {
            throw new StaccatoException("올바르지 않은 닉네임 형식입니다.");
        }
    }
}
