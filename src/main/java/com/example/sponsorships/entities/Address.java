package com.example.sponsorships.entities;

import com.example.sponsorships.enums.CITY;

/**
 * Predstavlja adresu s ulicom, kućnim brojem i gradom.
 */
public class Address {
    private final Long id;
    private final String streetName;
    private final String houseNumber;
    private final CITY city;

    /**
     * Konstruktor za kreiranje adrese.
     *
     * @param id jedinstveni identifikator adrese
     * @param streetName naziv ulice
     * @param houseNumber broj kuće
     * @param city grad iz enumeracije CITY
     */
    public Address(Long id, String streetName, String houseNumber, CITY city) {
        this.id = id;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.city = city;
    }

    /**
     * Dohvaća naziv ulice.
     *
     * @return naziv ulice
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Dohvaća broj kuće.
     *
     * @return broj kuće
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * Dohvaća grad.
     *
     * @return grad iz enumeracije CITY
     */
    public CITY getCity() {
        return city;
    }

    /**
     * Dohvaća jedinstveni identifikator adrese.
     *
     * @return id adrese
     */
    public Long getId() {
        return id;
    }

    /**
     * Vraća string reprezentaciju adrese u formatu:
     * "ulica broj, grad".
     *
     * @return string reprezentacija adrese
     */
    @Override
    public String toString() {
        return this.streetName + " " + this.houseNumber + ", " + this.city.getCityName();
    }
}
