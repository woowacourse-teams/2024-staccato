package com.staccato.exception;

public class StaccatoException extends RuntimeException {
    public StaccatoException() {
        super();
    }

    public StaccatoException(String message) {
        super(message);
    }

    public StaccatoException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaccatoException(Throwable cause) {
        super(cause);
    }
}
