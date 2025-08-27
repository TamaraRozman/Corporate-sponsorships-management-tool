package com.example.sponsorships.records;

import com.example.sponsorships.utils.FileUtils;
/**
 * Zapis koji predstavlja korisnika aplikacije.
 * Sadrži korisničko ime, lozinku i informaciju je li korisnik administrator.
 *
 * @param username korisničko ime
 * @param password korisnička lozinka (u nešifriranom obliku, šifrira se prilikom ispisa)
 * @param admin označava je li korisnik administrator
 */
public record User(String username, String password, boolean admin) {
    /**
     * Vraća string reprezentaciju korisnika u CSV formatu.
     * Lozinka se prilikom ispisa šifrira pomoću SHA-256 algoritma.
     *
     * @return CSV redak sa šifriranom lozinkom i podacima korisnika
     */
    @Override
    public String toString() {
        return username + "," + FileUtils.hashPassword(password) + "," + admin;
    }
}
