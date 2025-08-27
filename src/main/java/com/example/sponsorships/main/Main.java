package com.example.sponsorships.main;

import com.example.sponsorships.utils.ExtensionHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Glavna klasa JavaFX aplikacije koja pokreće početni zaslon za prijavu
 * i inicijalizira HTTP server za primanje zahtjeva za produženje programa.
 */
public class Main extends Application {

    private static Stage mainStage;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Pokreće aplikaciju te otvara zaslon za prijavu.
     *
     * @param stage glavni prozor (stage) u kojem će se vrtiti cijela aplikacija
     * @throws IOException baca iznimku ako se dogodi problem s pokretanjem stage-a,
     *                     primjerice ako ne može pronaći potrebni FXML file
     */
    @Override
    public void start(Stage stage) throws IOException {
        setMainStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/sponsorships/loginScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);

        stage.setTitle("Prijava");
        stage.setScene(scene);
        stage.show();
        logger.info("Aplikacija je pokrenuta.");
    }

    /**
     * Ulazna točka aplikacije.
     * Pokreće HTTP server na portu 8080 koji prima zahtjeve za produženje programa
     * te zatim pokreće JavaFX aplikaciju.
     *
     * @param args argumenti komandne linije (ne koriste se)
     */
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/extension-response", new ExtensionHandler());
            server.start();
        }
        catch (IOException e){
            logger.error("HTTP server failed to start. {}", e.getMessage());
        }

        launch();
    }

    /**
     * Postavlja glavni stage aplikacije.
     *
     * @param stage stage koji se postavlja kao glavni
     */
    private static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    /**
     * Dohvaća glavni stage aplikacije.
     *
     * @return glavni stage
     */
    public static Stage getMainStage() {
        return mainStage;
    }
}