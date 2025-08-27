package com.example.sponsorships.controllers;

import com.example.sponsorships.main.Main;
import com.example.sponsorships.utils.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Kontroler glavnog menija aplikacije.
 * <p>
 * Upravljanje prikazom i interakcijom sa različitim ekranima kroz meni.
 * Također upravlja pravima pristupa administratora i funkcijom odjave.
 * </p>
 */
public class MenuController {

    @FXML
    public Menu adminMenu;

    Stage mainStage = Main.getMainStage();

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    /**
     * Inicijalizacija kontrolera.
     * Onemogućava pristup admin meniju ako prijavljeni korisnik nije administrator.
     */
    public void initialize() {
        Platform.runLater(() -> {
            Session s = Session.getSession();
            if (s != null && !s.getCurrentUser().admin()) {
                adminMenu.setDisable(true);
            }
        });
    }

    /**
     * Odjava korisnika i vraćanje na ekran za prijavu.
     */
    public void logout() {
        try {
            Session.endSession();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/sponsorships/loginScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 925, 650);

            mainStage.setTitle("Prijava");
            mainStage.setScene(scene);
            mainStage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Otvara ekran sa prikazom promjena (changes).
     */
    public void openChangesScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/sponsorships/changesScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 950, 650);

            Stage stage = mainStage;
            stage.setTitle("Changes");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Otvara ekran sa spiskom sponzora.
     */
    public void openSponsorsScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/sponsorships/sponsorsScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 950, 650);

            Stage stage = mainStage;
            stage.setTitle("Sponsors");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Otvara ekran sa spiskom programa.
     */
    public void openProgramsScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/sponsorships/programsScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 950, 650);

            Stage stage = mainStage;
            stage.setTitle("Programs");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Otvara ekran sa statistikom.
     */
    public void openStatisticsScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/sponsorships/statisticsScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 950, 650);

            Stage stage = mainStage;
            stage.setTitle("Programs");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
