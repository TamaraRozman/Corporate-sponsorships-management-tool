package com.example.sponsorships.entities;

import com.example.sponsorships.interfaces.DataExportable;
import com.example.sponsorships.interfaces.Extendable;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;

/**
 * Predstavlja program sponzorstva s informacijama o sponzoru, dnevnom iznosu,
 * opisu, datumu početka i završetka.
 * Implementira sučelja {@link Extendable} i {@link DataExportable}.
 */
public final class Program extends NamedEntity implements Extendable, DataExportable {

    Sponsor sponsor;
    Long dailyAmount;
    String description;
    LocalDate startDate;
    LocalDate endDate;

    /**
     * Konstruktor koji inicijalizira program s ID-em i imenom.
     *
     * @param id jedinstveni identifikator programa
     * @param ime naziv programa
     */
    public Program(Long id, String ime) {
        super(id, ime);
    }

    /**
     * Konstruktor koji inicijalizira sve atribute programa.
     *
     * @param id jedinstveni identifikator programa
     * @param ime naziv programa
     * @param sponsor sponzor programa
     * @param dailyAmount dnevni iznos sponzorstva
     * @param description opis programa
     * @param startDate datum početka programa
     * @param endDate datum završetka programa
     */
    public Program(Long id, String ime, Sponsor sponsor, Long dailyAmount, String description, LocalDate startDate, LocalDate endDate) {
        super(id, ime);
        this.sponsor = sponsor;
        this.dailyAmount = dailyAmount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Dohvaća sponzora programa.
     *
     * @return sponzor programa
     */
    public Sponsor getSponsor() {
        return sponsor;
    }

    /**
     * Postavlja sponzora programa.
     *
     * @param sponsor novi sponzor
     */
    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    /**
     * Dohvaća dnevni iznos sponzorstva.
     *
     * @return dnevni iznos
     */
    public Long getDailyAmount() {
        return dailyAmount;
    }

    /**
     * Postavlja dnevni iznos sponzorstva.
     *
     * @param dailyAmount novi dnevni iznos
     */
    public void setDailyAmount(Long dailyAmount) {
        this.dailyAmount = dailyAmount;
    }

    /**
     * Dohvaća opis programa.
     *
     * @return opis programa
     */
    public String getDescription() {
        return description;
    }

    /**
     * Postavlja opis programa.
     *
     * @param description novi opis
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Dohvaća datum početka programa.
     *
     * @return datum početka
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Postavlja datum početka programa.
     *
     * @param startDate novi datum početka
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Dohvaća datum završetka programa.
     *
     * @return datum završetka
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Postavlja datum završetka programa.
     *
     * @param endDate novi datum završetka
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Izračunava broj dana trajanja programa, uključujući i početni i završni dan.
     *
     * @return broj dana trajanja programa ili 0 ako datumi nisu postavljeni
     */
    public long getDaysOfProgram() {
        if (startDate != null && endDate != null) {
            return ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return 0;
    }

    /**
     * Izračunava ukupni iznos sponzorstva za cijelo trajanje programa.
     *
     * @return ukupni iznos
     */
    public Long getFullAmount(){
        return dailyAmount * getDaysOfProgram();
    }

    /**
     * Produžuje datum završetka programa za zadani broj dana.
     *
     * @param days broj dana za koje se produžuje rok
     */
    @Override
    public void extendDueDateBy(int days) {
        endDate = endDate.plusDays(days);
    }

    /**
     * Vraća String reprezentaciju programa.
     *
     * @return niz znakova koji opisuje program
     */
    @Override
    public String toString() {
        return getName() + ", " + getDescription() + ", " + getStartDate() + " - " + getEndDate() + ", " + getSponsor().getName();
    }

    /**
     * Eksportira podatke programa u CSV format.
     *
     * @return String u CSV formatu koji predstavlja program
     */
    @Override
    public String exportAsCSV() {
        return toString();
    }
}
