package com.example.sponsorships.utils;

import com.example.sponsorships.records.User;

/**
 * Singleton klasa koja predstavlja korisničku sesiju.
 * <p>
 * Omogućuje spremanje i dohvat trenutno prijavljenog korisnika unutar aplikacije.
 * Sesiju je moguće kreirati samo jednom (prva prijava), nakon čega se ista instanca koristi globalno.
 * </p>
 */
public class Session {
    private static Session instance;
    private final User currentUser;

    /**
     * Privatni konstruktor onemogućuje izravno instanciranje sesije izvana.
     * Sesija se mora stvoriti preko {@link #createSession(User)} metode.
     *
     * @param user trenutno prijavljeni korisnik
     */
    private Session(User user) {
        this.currentUser = user;
    }

    /**
     * Kreira novu sesiju za danog korisnika ako već ne postoji instanca sesije.
     *
     * @param user korisnik koji se prijavljuje
     * @return jedinstvena instanca sesije
     */
    public static Session createSession(User user) {
        if (instance == null) {
            instance = new Session(user);
        }
        return instance;
    }

    /**
     * Vraća trenutnu instancu sesije.
     *
     * @return instanca sesije ili {@code null} ako nije inicijalizirana
     */
    public static Session getSession() {
        return instance;
    }

    /**
     * Vraća korisnika povezanog s trenutnom sesijom.
     *
     * @return trenutno prijavljeni korisnik
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Zatvara sesiju tako da poništava instancu.
     * Nakon poziva ove metode, mora se ponovno pozvati {@link #createSession(User)} za novu sesiju.
     */
    public static void endSession() {
        instance = null;
    }
}
