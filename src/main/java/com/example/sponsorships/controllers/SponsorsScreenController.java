package com.example.sponsorships.controllers;

import com.example.sponsorships.entities.*;
import com.example.sponsorships.enums.CITY;
import com.example.sponsorships.utils.ChangeManager;
import com.example.sponsorships.utils.DatabaseUtils;
import com.example.sponsorships.utils.Session;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * Kontroler za ekran upravljanja sponzorima.
 * Omogućuje prikaz, dodavanje, uređivanje i brisanje sponzora,
 * te upravljanje podacima o kontakt osobi i adresi sponzora.
 */
public class SponsorsScreenController {
    @FXML TableView<Sponsor> tableView;
    @FXML TableColumn<Sponsor, String> nameColumn;
    @FXML
    TableColumn<Sponsor, String> idColumn;
    @FXML
    TableColumn<Sponsor, String> addressColumn;
    @FXML
    TableColumn<Sponsor, String> personColumn;
    @FXML
    TableColumn<Sponsor, String> emailColumn;
    @FXML TableColumn<Sponsor, Void> actionColumn;
    @FXML TextField sponsorNameField;
    @FXML
    TextField sponsorStreetField;
    @FXML
    TextField sponsorHouseNumberField;
    @FXML
    TextField emailField;
    @FXML
    TextField contactPersonNameField;
    @FXML
    TextField contactPersonSurameField;
    @FXML
    TextField contactPersonStreetField;
    @FXML
    TextField contactPersonHouseNumberField;
    @FXML ComboBox<CITY> sponsorCityCB;
    @FXML
    ComboBox<CITY> contactCityCB;
    @FXML DatePicker datePicker;
    @FXML Label errorLabel;
    @FXML Button addButton;
    @FXML
    Button saveButton;

    private ObservableList<Sponsor> sponsorsObsList;
    private Sponsor editingSponsor = null;

    /**
     * Inicijalizira kontroler, postavlja podatke u comboBox-ove i tablicu,
     * te priprema korisničko sučelje za rad.
     */
    public void initialize() {
        var cities = FXCollections.observableArrayList((CITY)null);
        cities.addAll(CITY.values());
        sponsorCityCB.setItems(cities);
        contactCityCB.setItems(cities);
        errorLabel.setVisible(false);
        sponsorsObsList = FXCollections.observableArrayList(DatabaseUtils.loadSponsors());
        setupTable();
        saveButton.setVisible(false);
    }

    /**
     * Konfigurira stupce i akcije u tablici za prikaz sponzora.
     */
    private void setupTable() {
        tableView.setItems(sponsorsObsList);
        nameColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getName()));
        idColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getId().toString()));
        personColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getContactPerson().getName() + " " + c.getValue().getContactPerson().getSurname()));
        addressColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getAddress().toString()));
        emailColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEmail()));

        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = createButton("Edit", "edit-button", e -> loadSponsorToForm(getCurrentSponsor()));
            private final Button deleteBtn = createButton("Delete", "delete-button", e -> confirmDelete(getCurrentSponsor()));
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            private Sponsor getCurrentSponsor() { return getTableView().getItems().get(getIndex()); }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    /**
     * Kreira dugme s definiranim tekstom, stilom i akcijom.
     * @param text tekst na dugmetu
     * @param styleClass CSS klasa za stilizaciju
     * @param handler događaj koji se izvršava klikom na dugme
     * @return novo kreirano dugme
     */
    private Button createButton(String text, String styleClass, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.getStyleClass().add(styleClass);
        btn.setOnAction(handler);
        return btn;
    }

    /**
     * Prikazuje dijalog za potvrdu brisanja sponzora.
     * @param sponsor sponzor koji se briše
     */
    private void confirmDelete(Sponsor sponsor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Pritiskom na \"OK\" nepovratno ćete izbrisati ovog sponzora iz baze podataka.",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Potvrdite brisanje");
        alert.setHeaderText("Potvrdite brisanje sponzora iz baze podataka.");

        alert.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(btn -> {
            DatabaseUtils.deleteById("sponsors", sponsor.getId());
            sponsorsObsList.remove(sponsor);
            ChangeManager.addNewChange(new Change("Izbrisan sponzor " + sponsor.getName(), Session.getSession().getCurrentUser().username(), sponsor.getName(), ""));
            if (sponsor.equals(editingSponsor)) clearEditMode();
        });
    }

    /**
     * Učitava podatke odabranog sponzora u formu za uređivanje.
     * @param sponsor sponzor čiji se podaci učitavaju
     */
    private void loadSponsorToForm(Sponsor sponsor) {
        editingSponsor = sponsor;
        sponsorNameField.setText(sponsor.getName());
        emailField.setText(sponsor.getEmail());
        contactPersonNameField.setText(sponsor.getContactPerson().getName());
        contactPersonSurameField.setText(sponsor.getContactPerson().getSurname());
        contactPersonStreetField.setText(sponsor.getAddress().getStreetName());
        contactPersonHouseNumberField.setText(sponsor.getAddress().getHouseNumber());
        contactCityCB.setValue(sponsor.getAddress().getCity());
        sponsorCityCB.setValue(sponsor.getAddress().getCity());
        sponsorStreetField.setText(sponsor.getAddress().getStreetName());
        sponsorHouseNumberField.setText(sponsor.getAddress().getHouseNumber());
        datePicker.setValue(sponsor.getContactPerson().getDateOfBirth());
        addButton.setVisible(false);
        saveButton.setVisible(true);
        errorLabel.setVisible(false);
    }

    /**
     * Spremanje uređivanja sponzora nakon potvrde.
     */
    @FXML
    private void saveEditedSponsor() {
        if (editingSponsor == null || !fieldCheck()) {
            errorLabel.setVisible(true);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Pritiskom na \"OK\" spremit ćete promjene.",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Potvrdite spremanje");
        alert.setHeaderText("Potvrdite spremanje promjena sponzora.");
        alert.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(btn -> {

            Person updatedPerson = new Person(
                    editingSponsor.getContactPerson().getId(),
                    contactPersonNameField.getText(),
                    contactPersonSurameField.getText(),
                    datePicker.getValue(),
                    new Address(
                            editingSponsor.getContactPerson().getAddress().getId(),
                            contactPersonStreetField.getText(),
                            contactPersonHouseNumberField.getText(),
                            contactCityCB.getValue()
                    )
            );
            Address updatedAddress = new AddressBuilder()
                    .setId(editingSponsor.getAddress().getId())
                    .setCity(sponsorCityCB.getValue())
                    .setStreetName(sponsorStreetField.getText())
                    .setHouseNumber(sponsorHouseNumberField.getText())
                    .createAddress();

            Sponsor updatedSponsor = new Sponsor(
                    editingSponsor.getId(),
                    sponsorNameField.getText(),
                    emailField.getText(),
                    updatedAddress,
                    updatedPerson
            );

            DatabaseUtils.updateSponsor(updatedSponsor);

            ChangeManager.addNewChange(new Change(
                    "Uređen sponzor " + editingSponsor.getName(),
                    Session.getSession().getCurrentUser().username(),
                    editingSponsor.toString(),
                    updatedSponsor.toString()
            ));

            refreshTable();
            clearEditMode();
        });
    }

    /**
     * Briše način uređivanja i resetira formu.
     */
    private void clearEditMode() {
        editingSponsor = null;
        clearData();
        addButton.setVisible(true);
        saveButton.setVisible(false);
        errorLabel.setVisible(false);
    }

    /**
     * Provjerava jesu li svi potrebni podaci u formi ispravno uneseni.
     * @return true ako su svi podaci uneseni, false inače
     */
    private boolean fieldCheck() {
        return !sponsorNameField.getText().isEmpty() && !sponsorStreetField.getText().isEmpty() && !sponsorHouseNumberField.getText().isEmpty()
                && !emailField.getText().isEmpty() && !contactPersonNameField.getText().isEmpty() && !contactPersonSurameField.getText().isEmpty()
                && !contactPersonStreetField.getText().isEmpty() && !contactPersonHouseNumberField.getText().isEmpty()
                && sponsorCityCB.getValue() != null && contactCityCB.getValue() != null
                && datePicker.getValue() != null;
    }

    /**
     * Briše sve unose u formi i skriva poruku o grešci.
     */
    private void clearData() {
        sponsorNameField.clear();
        sponsorStreetField.clear();
        sponsorHouseNumberField.clear();
        contactPersonNameField.clear();
        contactPersonSurameField.clear();
        contactPersonStreetField.clear();
        contactPersonHouseNumberField.clear();
        contactCityCB.setValue(null);
        sponsorCityCB.setValue(null);
        datePicker.setValue(null);
        emailField.clear();
        errorLabel.setVisible(false);
    }

    /**
     * Dodaje novog sponzora u bazu podataka nakon potvrde.
     */
    public void addSponsor() {
        if (!fieldCheck()) {
            errorLabel.setVisible(true);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Pritiskom na \"OK\" dodat ćete novog sponzora u bazu podataka.",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Potvrdite unos");
        alert.setHeaderText("Potvrdite dodavanje sponzora u bazu podataka.");

        alert.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(btn -> {
            DatabaseUtils.addSponsor(sponsorNameField.getText(), emailField.getText(),
                    new Person(0L, contactPersonNameField.getText(), contactPersonSurameField.getText(), datePicker.getValue(), null),
                    new Address(0L, contactPersonStreetField.getText(), contactPersonHouseNumberField.getText(), contactCityCB.getValue()),
                    sponsorCityCB.getValue().getCityName(), sponsorStreetField.getText(), sponsorHouseNumberField.getText());

            List<Change> promjene = ChangeManager.loadUserActions();
            promjene.add(new Change("Dodavanje sponzora", Session.getSession().getCurrentUser().username(), sponsorNameField.getText(), ""));
            ChangeManager.saveUserAction(promjene);

            clearData();
            refreshTable();
        });
    }

    /**
     * Osvježava sadržaj tablice sponzora iz baze podataka.
     */
    private void refreshTable() {
        sponsorsObsList.setAll(DatabaseUtils.loadSponsors());
        tableView.refresh();
    }
}
