package com.example.sponsorships.utils;

import java.util.UUID;

/**
 * Pomoćna klasa za generiranje jedinstvenih identifikacijskih tokena.
 * <p>
 * Klasa koristi {@link UUID} kako bi generirala sigurne i nasumične stringove
 * koji se mogu koristiti kao tokeni za potvrdu, sesije, produženja i sl.
 * </p>
 */
public class TokenGenerator {

    /**
     * Privatni konstruktor sprječava instanciranje ove klase jer sadrži samo statičke metode.
     */
    private TokenGenerator() {}

    /**
     * Generira novi jedinstveni token u obliku UUID stringa.
     *
     * @return jedinstveni identifikator u string formatu
     */
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
