package com.example.sponsorships.controllers;

import com.example.sponsorships.entities.Change;
import com.example.sponsorships.exceptions.UsernameTakenException;
import com.example.sponsorships.main.Main;
import com.example.sponsorships.records.User;
import com.example.sponsorships.utils.ChangeManager;
import com.example.sponsorships.utils.FileUtils;
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
import java.util.List;

/**
 * Kontroler za registraciju korisnika koji omogućuje unos korisničkih podataka,
 * validaciju, pohranu novog korisnika i automatski prijelaz na glavni ekran nakon uspješne registracije.
 */
public class RegistrationController {

    @FXML
    TextField nameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField confirmPasswordField;
    @FXML
    Label messageLabel;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    /**
     * Metoda koja obavlja registraciju novog korisnika,
     * provjerava valjanost unesenih podataka, sprema korisnika u datoteku
     * te nakon uspješne registracije učitava glavni zaslon aplikacije.
     */
    public void registerAndLogin(){
        if(usernameTextField.getText() == null || passwordField.getText() == null || confirmPasswordField.getText() == null || !passwordField.getText().equals(confirmPasswordField.getText())){
            messageLabel.setText("Registration failed. Please check the input fields.");
        }
        else{
            try{
                FileUtils.saveUserToFile(new User(usernameTextField.getText(), passwordField.getText(), false));
                List<Change> promjene = ChangeManager.loadUserActions();
                promjene.add(new Change("Registriran novi korisnik " + usernameTextField.getText(), usernameTextField.getText(), "", usernameTextField.getText()));
                ChangeManager.saveUserAction(promjene);

                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 950, 650);
                Stage stage = Main.getMainStage();
                stage.setTitle("Sign up");
                stage.setScene(scene);
                stage.show();
            }
            catch (IOException | UsernameTakenException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Metoda koja prebacuje korisnika na ekran za prijavu.
     */
    public void goToLogin(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/sponsorships/loginScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 950, 650);
            Stage stage = Main.getMainStage();
            stage.setTitle("Prijava");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e){
            logger.error(e.getMessage());
        }
    }
}
