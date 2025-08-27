package com.example.sponsorships.utils;

import com.example.sponsorships.entities.Change;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pomoćna klasa za upravljanje promjenama korisničkih akcija.
 * Omogućuje serijalizaciju i deserijalizaciju objekata tipa {@link Change} u binarnu datoteku.
 * Sadrži metode za spremanje, učitavanje i dodavanje promjena.
 * <p>
 * Klasa koristi sinkronizaciju kako bi bila sigurna za višedretveno izvođenje.
 */
public class ChangeManager {

    /** Privatni konstruktor da bi se onemogućilo instanciranje ove pomoćne klase. */
    private ChangeManager(){}

    private static final String FILE_PATH = "files/changesFile.bin";
    private static final Logger logger = LoggerFactory.getLogger(ChangeManager.class);

    /**
     * Sprema listu promjena u binarnu datoteku.
     *
     * @param promjene lista objekata tipa {@link Change} koji se serijaliziraju i spremaju u datoteku
     */
    public static synchronized void saveUserAction(List<Change> promjene) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(promjene);
            logger.info("Spremljena promjena.");
        } catch (IOException e) {
            logger.error("Pogreška pri serijalizaciji promjena.", e);
        }
    }

    /**
     * Učitava i deserijalizira listu promjena iz binarne datoteke.
     *
     * @return lista promjena korisničkih akcija; ako datoteka ne postoji, vraća se prazna lista
     */
    public static synchronized List<Change> loadUserActions() {
        List<Change> userActions = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return userActions;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            userActions = (List<Change>) ois.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            logger.error("Pogreška pri deserijalizaciji promjena.", e);
        }
        return userActions;
    }

    /**
     * Dodaje novu promjenu u postojeću listu i sprema ažuriranu listu u datoteku.
     *
     * @param change nova promjena koja se dodaje u listu
     */
    public static synchronized void addNewChange(Change change){
        List<Change> promjene = loadUserActions();
        promjene.add(change);
        saveUserAction(promjene);
    }
}
