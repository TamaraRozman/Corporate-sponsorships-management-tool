package com.example.sponsorships.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

/***
 * Predstavlja promjenu napravljenu u aplikaciji npr. dodan novi korisnik, uređeni detalji sponzorstva itd.
 * Svakoj promjeni potrebno je dodijeliti opis, a automatski joj se dodjeljuje vrijeme kad je učinjena.
 */
public class Change implements Serializable {
    private final String description;
    private final LocalDateTime timestamp;
    private final String user;
    private final String oldValue;
    private final String newValue;

    /**
     * Konstruktor za kreiranje nove promjene.
     *
     * @param description opis promjene
     * @param user korisnik koji je napravio promjenu
     * @param oldValue prethodna vrijednost (prije promjene)
     * @param newValue nova vrijednost (nakon promjene)
     */
    public Change(String description, String user, String oldValue, String newValue) {
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.user = user;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Dohvaća opis promjene.
     *
     * @return opis promjene
     */
    public String getDescription() {
        return description;
    }

    /**
     * Dohvaća vrijeme kada je promjena napravljena.
     *
     * @return vremenska oznaka promjene
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Dohvaća korisnika koji je napravio promjenu.
     *
     * @return korisničko ime ili identifikator
     */
    public String getUser() {
        return user;
    }

    /**
     * Dohvaća staru vrijednost prije promjene.
     *
     * @return prethodna vrijednost
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * Dohvaća novu vrijednost nakon promjene.
     *
     * @return nova vrijednost
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * Vraća tekstualni prikaz promjene.
     *
     * @return string koji sadrži vremensku oznaku, opis i korisnika
     */
    @Override
    public String toString() {
        return "[" + timestamp.toString() + "] " + description
                + " " + user + " ";
    }
}
