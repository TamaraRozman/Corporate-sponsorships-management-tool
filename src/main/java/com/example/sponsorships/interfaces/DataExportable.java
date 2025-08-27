package com.example.sponsorships.interfaces;
/**
 * Sučelje zajedničko za objekte koji se mogu izvesti u CSV format.
 * Implementirajuće klase moraju pružiti implementaciju metode {@code exportAsCSV()}
 * koja vraća podatke objekta u obliku CSV (Comma-Separated Values) stringa.
 */
public interface DataExportable {
    String exportAsCSV();
}
