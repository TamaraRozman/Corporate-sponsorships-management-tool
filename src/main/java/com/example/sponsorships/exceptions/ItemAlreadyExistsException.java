package com.example.sponsorships.exceptions;

/**
 * Iznimka koja označava da element koji se pokušava dodati već postoji.
 */
public class ItemAlreadyExistsException extends Exception{
    public ItemAlreadyExistsException() {
    }

    public ItemAlreadyExistsException(String message) {
        super(message);
    }

    public ItemAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
