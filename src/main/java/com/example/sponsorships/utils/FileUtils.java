package com.example.sponsorships.utils;

import com.example.sponsorships.exceptions.NoPropertyFoundException;
import com.example.sponsorships.exceptions.UsernameTakenException;
import com.example.sponsorships.exceptions.NoSuchUserException;
import com.example.sponsorships.records.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * {@code FileUtils} je pomoćna klasa za upravljanje datotekama vezanim uz korisnike.
 * <p>
 * Sadrži metode za hashiranje lozinke, spremanje korisnika, provjeru korisničkih podataka i dohvat korisnika
 * iz tekstualne datoteke. Također uključuje rukovanje specifičnim iznimkama kao što su {@link UsernameTakenException},
 * {@link NoPropertyFoundException} i {@link NoSuchUserException}.
 * </p>
 */
public class FileUtils {

    /***
     * Privatan konstruktor da spriječi stvaranje instance FileUtils objekta.
     */
    private FileUtils() {}

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String FILEPATH = "files/users.txt";

    /**
     * Hashira danu lozinku korištenjem SHA-256 algoritma i kodira ju u Base64.
     *
     * @param password lozinka za hashiranje
     * @return hashirana i Base64-kodirana lozinka
     * @throws IllegalStateException ako algoritam nije podržan
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error hashing password", e);
        }
    }

    /**
     * Sprema korisnika u datoteku, ali prije toga provjerava postoji li korisnik s istim usernameom.
     *
     * @param user korisnik koji se sprema
     * @throws UsernameTakenException ako username već postoji u datoteci
     */
    public static void saveUserToFile(User user) throws UsernameTakenException {
        if (doesUserExist(user.username())) {
            throw new UsernameTakenException("Username '" + user.username() + "' već postoji.");
        }

        String hashedPassword = hashPassword(user.password());
        try (FileWriter fw = new FileWriter(FILEPATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(user.username() + "," + hashedPassword + "," + user.admin());
            bw.newLine();

        } catch (IOException e) {
            logger.error("Pogreška pri spremanju korisnika.", e);
        }
    }

    /**
     * Provjerava postoji li korisnik s određenim korisničkim imenom u datoteci.
     *
     * @param username korisničko ime za provjeru
     * @return {@code true} ako korisnik postoji, inače {@code false}
     * @throws NoPropertyFoundException ako podaci o korisniku nisu potpuni
     */
    private static boolean doesUserExist(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILEPATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    throw new NoPropertyFoundException("Nepotpuni podaci u datoteci za korisnika.");
                }
                if (parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error("Greška prilikom provjere korisnika: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Provjerava korisničko ime i lozinku na temelju sadržaja datoteke.
     *
     * @param enteredUsername uneseni username
     * @param enteredPassword unesena lozinka
     * @return {@code true} ako je autentifikacija uspješna, inače {@code false}
     * @throws NoPropertyFoundException ako nedostaju potrebni atributi korisnika
     */
    public static boolean checkUser(String enteredUsername, String enteredPassword){
        try (BufferedReader reader = new BufferedReader(new FileReader(FILEPATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    throw new NoPropertyFoundException("Nepotpuni podaci u datoteci za korisnika.");
                }
                String username = parts[0];
                String storedPassword = parts[1];

                if (username.equals(enteredUsername)) {
                    String hashedEnteredPassword = hashPassword(enteredPassword);
                    return hashedEnteredPassword.equals(storedPassword);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * Dohvaća korisnika na temelju korisničkog imena i lozinke.
     *
     * @param enteredUsername uneseni username
     * @param enteredPassword unesena lozinka
     * @return {@link User} objekt ako su podaci ispravni
     * @throws NoSuchUserException ako korisnik ne postoji ili je lozinka netočna
     * @throws NoPropertyFoundException ako podaci o korisniku nisu potpuni
     */
    public static User findUser(String enteredUsername, String enteredPassword) throws NoSuchUserException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILEPATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    throw new NoPropertyFoundException("Nepotpuni podaci u datoteci za korisnika.");
                }
                String username = parts[0];
                String storedPassword = parts[1];
                boolean admin = Boolean.parseBoolean(parts[2].trim());

                if (username.equals(enteredUsername)) {
                    String hashedEnteredPassword = hashPassword(enteredPassword);
                    if (hashedEnteredPassword.equals(storedPassword)) {
                        return new User(enteredUsername, enteredPassword, admin);
                    } else {
                        throw new NoSuchUserException("Neispravna lozinka za korisnika.");
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        throw new NoSuchUserException("Korisnik '" + enteredUsername + "' nije pronađen.");
    }
}
