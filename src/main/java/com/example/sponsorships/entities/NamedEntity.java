package com.example.sponsorships.entities;

/**
 * Apstraktna klasa koja predstavlja entitet s imenom.
 * Sadrži jedinstveni identifikator i naziv entiteta.
 */
public abstract class NamedEntity {
    private Long id;
    private final String name;

    /**
     * Konstruktor koji postavlja ID i naziv entiteta.
     *
     * @param id jedinstveni identifikator entiteta
     * @param ime naziv entiteta
     */
    protected NamedEntity(Long id, String ime) {
        this.id = id;
        this.name = ime;
    }

    /**
     * Dohvaća jedinstveni identifikator entiteta.
     *
     * @return ID entiteta
     */
    public Long getId() {
        return id;
    }

    /**
     * Postavlja jedinstveni identifikator entiteta.
     *
     * @param id novi ID entiteta
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Dohvaća naziv entiteta.
     *
     * @return naziv entiteta
     */
    public String getName() {
        return name;
    }

}
