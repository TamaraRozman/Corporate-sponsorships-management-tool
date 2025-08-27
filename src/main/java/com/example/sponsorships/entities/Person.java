package com.example.sponsorships.entities;

import java.time.LocalDate;

/**
 * Predstavlja osobu koja nasljeđuje NamedEntity iz kojeg koristi id i ime,
 * a na to još nadodaje prezime, datum rođenja i adresu.
 */
public class Person extends NamedEntity {

    private final String surname;
    private final LocalDate dateOfBirth;
    private Address address;

    /**
     * Konstruktor koji inicijalizira osobu s ID-em, imenom, prezimenom, datumom rođenja i adresom.
     *
     * @param id jedinstveni identifikator osobe
     * @param ime ime osobe
     * @param surname prezime osobe
     * @param dateOfBirth datum rođenja osobe
     * @param address adresa osobe
     */
    public Person(Long id, String ime, String surname, LocalDate dateOfBirth, Address address) {
        super(id, ime);
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    /**
     * Dohvaća prezime osobe.
     *
     * @return prezime osobe
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Dohvaća datum rođenja osobe.
     *
     * @return datum rođenja osobe
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Dohvaća adresu osobe.
     *
     * @return adresa osobe
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Postavlja adresu osobe.
     *
     * @param address nova adresa osobe
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Metoda za jednostavan ispis osobe u String formatu.
     *
     * @return String koji se sastoji od imena, prezimena i datuma rođenja osobe
     */
    @Override
    public String toString() {
        return (super.getName() + " " + surname + ", " + dateOfBirth);
    }
}
