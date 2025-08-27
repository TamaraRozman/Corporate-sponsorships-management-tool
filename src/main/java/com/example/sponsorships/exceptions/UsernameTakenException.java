package com.example.sponsorships.exceptions;

/**
 * Iznimka koja označava da je korisničko ime zauzeto.
 */
public class UsernameTakenException extends Exception{
    public UsernameTakenException() {
    }

    public UsernameTakenException(String message) {
        super(message);
    }
}
