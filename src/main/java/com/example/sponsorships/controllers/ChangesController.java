package com.example.sponsorships.controllers;

import com.example.sponsorships.entities.Change;
import com.example.sponsorships.utils.ChangeManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontroler zadužen za prikaz promjena unutar korisničkog sučelja.
 * <p>
 * Dohvaća promjene iz serijalizirane datoteke putem {@link ChangeManager}
 * i prikazuje ih u tabličnom prikazu koristeći JavaFX {@link TableView}.
 * </p>
 */
public class ChangesController {

    private static final Logger logger = LoggerFactory.getLogger(ChangesController.class);

    @FXML
    private TableView<Change> changesTableView;

    @FXML
    private TableColumn<Change, String> changeTableColumn;

    @FXML
    private TableColumn<Change, String> dateTableColumn;

    @FXML
    private TableColumn<Change, String> userTableColumn;

    @FXML
    private TableColumn<Change, String> oldTableColumn;

    @FXML
    private TableColumn<Change, String> newTableColumn;

    /**
     * Inicijalizacijska metoda koja se poziva automatski nakon učitavanja FXML-a.
     * Pokreće asinhroni zadatak za dohvat svih promjena i postavlja podatke u tablicu.
     */
    @FXML
    public void initialize() {
        Task<ObservableList<Change>> loadChangesTask = new Task<>() {
            @Override
            protected ObservableList<Change> call() {
                return FXCollections.observableArrayList(ChangeManager.loadUserActions());
            }
        };

        loadChangesTask.setOnSucceeded(event -> {
            changesTableView.setItems(loadChangesTask.getValue());
            logger.info("Changes loaded successfully.");
        });

        loadChangesTask.setOnFailed(event ->
                logger.error("Error loading changes: {}", loadChangesTask.getException().getMessage())
        );

        setupTable();
        Thread thread = new Thread(loadChangesTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Povezuje stupce tablice s odgovarajućim atributima objekta {@link Change}
     * korištenjem {@link ReadOnlyStringWrapper} kako bi se prikazao tekst u sučelju.
     */
    public void setupTable() {
        changeTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDescription()));
        dateTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getTimestamp().toString()));
        userTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getUser()));
        oldTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getOldValue()));
        newTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getNewValue()));
    }
}
