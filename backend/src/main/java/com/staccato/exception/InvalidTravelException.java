package com.staccato.exception;

public class InvalidTravelException extends RuntimeException {
    public InvalidTravelException() {
        super();
    }

    public InvalidTravelException(String message) {
        super(message);
    }

    public InvalidTravelException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTravelException(Throwable cause) {
        super(cause);
    }
}
