package com.example.sponsorships.entities;

import com.example.sponsorships.enums.CITY;

/**
 * Builder klasa za kreiranje objekta {@link Address}.
 */
public class AddressBuilder {
    private Long id;
    private String streetName;
    private String houseNumber;
    private CITY city;

    /**
     * Postavlja ID adrese.
     *
     * @param id jedinstveni identifikator adrese
     * @return trenutni objekt {@code AddressBuilder} radi lančanja poziva
     */
    public AddressBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Postavlja naziv ulice.
     *
     * @param streetName naziv ulice
     * @return trenutni objekt {@code AddressBuilder} radi lančanja poziva
     */
    public AddressBuilder setStreetName(String streetName) {
        this.streetName = streetName;
        return this;
    }

    /**
     * Postavlja broj kuće.
     *
     * @param houseNumber broj kuće
     * @return trenutni objekt {@code AddressBuilder} radi lančanja poziva
     */
    public AddressBuilder setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    /**
     * Postavlja grad iz enumeracije {@link CITY}.
     *
     * @param city grad
     * @return trenutni objekt {@code AddressBuilder} radi lančanja poziva
     */
    public AddressBuilder setCity(CITY city) {
        this.city = city;
        return this;
    }

    /**
     * Kreira novi objekt {@link Address} koristeći trenutno postavljene vrijednosti.
     *
     * @return novi objekt {@code Address}
     */
    public Address createAddress() {
        return new Address(id, streetName, houseNumber, city);
    }
}
