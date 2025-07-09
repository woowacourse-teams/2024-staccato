package com.staccato.exception;

public class ConflictException extends RuntimeException {
    public ConflictException() {
        super("요청하신 데이터가 다른 요청에 의해 수정되었습니다. 새로고침 후 다시 시도해주세요.");
    }

    public ConflictException(final String message) {
        super(message);
    }

    public ConflictException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConflictException(final Throwable cause) {
        super("요청하신 데이터가 다른 요청에 의해 수정되었습니다. 새로고침 후 다시 시도해주세요.", cause);
    }
}
