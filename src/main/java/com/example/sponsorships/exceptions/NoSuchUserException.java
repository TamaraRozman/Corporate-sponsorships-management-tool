package com.example.sponsorships.exceptions;

/**
 * Iznimka koja označava da takav korisnik nije pronađen u datoteci.
 */
public class NoSuchUserException extends RuntimeException{
    public NoSuchUserException(String message) {
        super(message);
    }

}
