package com.example.sponsorships.controllers;

import com.example.sponsorships.main.Main;
import com.example.sponsorships.records.User;
import com.example.sponsorships.utils.FileUtils;
import com.example.sponsorships.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Kontroler za ekran prijave (login screen).
 * <p>
 * Upravljanje unosom korisničkih podataka, provjerom autentifikacije te preusmjeravanjem
 * korisnika na glavni ekran aplikacije ili ekran registracije.
 * </p>
 */
public class LoginScreenController {

    private static final Logger logger = LoggerFactory.getLogger(LoginScreenController.class);
    Stage mainStage = Main.getMainStage();

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Label failedLoginLabel;

    /**
     * Metoda koja provjerava unesene podatke za prijavu.
     * Ako su podaci ispravni, korisnik se preusmjerava na početni ekran,
     * a sesija se kreira za prijavljenog korisnika.
     * U slučaju neuspjeha, ispisuje se poruka o neuspješnoj prijavi.
     */
    public void login() {
        if (FileUtils.checkUser(usernameTextField.getText(), passwordPasswordField.getText())) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/sponsorships/statisticsScreen.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 950, 650);

                Stage stage = Main.getMainStage();
                stage.setTitle("Home screen");
                stage.setScene(scene);
                stage.show();

                User currentUser = FileUtils.findUser(usernameTextField.getText(), passwordPasswordField.getText());
                Session.createSession(currentUser);

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } else {
            failedLoginLabel.setText("Login failed. please try again.");
        }
    }

    /**
     * Metoda za preusmjeravanje korisnika na ekran registracije.
     */
    public void goToRegistration() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginScreenController.class.getResource("/com/example/sponsorships/registration.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 500);

            if (mainStage == null) {
                logger.error("Registration stage is null!");
                return;
            }

            mainStage.setTitle("Sign up");
            mainStage.setScene(scene);
            mainStage.show();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
