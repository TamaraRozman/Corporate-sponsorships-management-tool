package com.example.sponsorships.exceptions;

/**
 * Iznimka koja označava da nije pronađena tražena svojstvo ili atribut.
 */
public class NoPropertyFoundException extends RuntimeException{
    public NoPropertyFoundException() {
    }

    public NoPropertyFoundException(String message) {
        super(message);
    }

    public NoPropertyFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPropertyFoundException(Throwable cause) {
        super(cause);
    }

    public NoPropertyFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
