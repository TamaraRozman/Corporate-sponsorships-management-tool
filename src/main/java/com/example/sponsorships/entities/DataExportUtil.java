package com.example.sponsorships.entities;

import com.example.sponsorships.interfaces.DataExportable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility generička klasa korištena za ispis objekata u CSV datoteku. Moguće je ispisati samo objekte koji implementiraju DataExportable sučelje.
 * @param <T>
 */
public class DataExportUtil <T extends DataExportable>{

    private static final Logger logger = LoggerFactory.getLogger(DataExportUtil.class);

    /**
     * Privatni defaultni konstruktor koji spječava stvaranje objekta bez parametara.
     */
    public DataExportUtil() { /* Privatni defaultni konstruktor koji spječava stvaranje objekta bez parametara */ }

    /**
     * Eksportira listu objekata koji implementiraju sučelje {@code DataExportable} u CSV datoteku.
     * Svaki objekt iz liste se konvertira u CSV redak pozivom metode {@code exportAsCSV()}.
     *
     * @param items lista objekata za izvoz u CSV format
     * @param filePath putanja i naziv datoteke u koju će se podaci spremiti
     */
    public boolean exportToCSV(List<T> items, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (T item : items) {
                writer.write(item.exportAsCSV() + "\n");
            }
            logger.info("Exportirano u CSV datoteku: {}", filePath);
            return true;
        } catch (IOException e) {
            logger.error("Greška prilikom izvoza u CSV: {}", e.getMessage());
            return false;
        }
    }
}
