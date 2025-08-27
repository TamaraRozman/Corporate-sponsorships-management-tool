package com.example.sponsorships.entities;

import com.example.sponsorships.interfaces.DataExportable;

/**
 * Predstavlja sponzora s osnovnim informacijama poput emaila, adrese i kontakt osobe.
 * Nasljeđuje {@link NamedEntity} i implementira {@link DataExportable}.
 */
public class Sponsor extends NamedEntity implements DataExportable {

    String email;
    Address address;
    Person contactPerson;

    /**
     * Konstruktor koji inicijalizira sponzora sa svim atributima.
     *
     * @param id jedinstveni identifikator sponzora
     * @param ime naziv sponzora
     * @param email email adresa sponzora
     * @param address adresa sponzora
     * @param contactPerson kontakt osoba za sponzora
     */
    public Sponsor(Long id, String ime, String email, Address address, Person contactPerson) {
        super(id, ime);
        this.email = email;
        this.address = address;
        this.contactPerson = contactPerson;
    }

    /**
     * Dohvaća email adresu sponzora.
     *
     * @return email adresa
     */
    public String getEmail() {
        return email;
    }

    /**
     * Postavlja email adresu sponzora.
     *
     * @param email nova email adresa
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Dohvaća adresu sponzora.
     *
     * @return adresa
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Postavlja adresu sponzora.
     *
     * @param address nova adresa
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Dohvaća kontakt osobu sponzora.
     *
     * @return kontakt osoba
     */
    public Person getContactPerson() {
        return contactPerson;
    }

    /**
     * Postavlja kontakt osobu sponzora.
     *
     * @param contactPerson nova kontakt osoba
     */
    public void setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
    }

    /**
     * Vraća naziv sponzora kao String.
     *
     * @return naziv sponzora
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Provjerava jednakost s drugim objektom na temelju ID-a.
     *
     * @param o objekt za usporedbu
     * @return true ako su isti, false inače
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sponsor sponsor = (Sponsor) o;

        return getId() != null && getId().equals(sponsor.getId());
    }

    /**
     * Vraća hash kod sponzora na temelju ID-a.
     *
     * @return hash kod
     */
    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    /**
     * Eksportira podatke sponzora u CSV format.
     *
     * @return String u CSV formatu koji predstavlja sponzora
     */
    @Override
    public String exportAsCSV() {
        return getName() + ", " + getEmail() + ", " + getAddress().toString() + ", " + getContactPerson().toString();
    }
}
