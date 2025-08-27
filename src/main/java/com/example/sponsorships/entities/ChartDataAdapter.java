package com.example.sponsorships.entities;

import java.util.List;
import java.util.function.Function;

/**
 * Adapter za pretvaranje liste objekata tipa T u listu objekata tipa R pomoću zadane funkcije mapiranja.
 *
 * @param <T> tip ulaznih objekata
 * @param <R> tip izlaznih objekata
 */
public class ChartDataAdapter<T, R> {
    private final Function<T, R> mappingFunction;

    /**
     * Konstruktor koji prima funkciju za mapiranje objekata tipa T u objekte tipa R.
     *
     * @param mappingFunction funkcija mapiranja
     */
    public ChartDataAdapter(Function<T, R> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    /**
     * Pretvara ulaznu listu objekata tipa T u listu objekata tipa R primjenom funkcije mapiranja.
     *
     * @param inputList lista ulaznih objekata
     * @return lista transformiranih objekata tipa R
     */
    public List<R> adapt(List<T> inputList) {
        return inputList.stream()
                .map(mappingFunction)
                .toList();
    }
}
