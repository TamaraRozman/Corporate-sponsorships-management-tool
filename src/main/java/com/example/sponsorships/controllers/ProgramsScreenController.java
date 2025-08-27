package com.example.sponsorships.controllers;

import com.example.sponsorships.entities.Change;
import com.example.sponsorships.entities.DataExportUtil;
import com.example.sponsorships.entities.Program;
import com.example.sponsorships.entities.Sponsor;
import com.example.sponsorships.exceptions.ItemAlreadyExistsException;
import com.example.sponsorships.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * Kontroler za ProgramsScreen koji upravlja prikazom i uređivanjem programa,
 * uključujući dodavanje, uređivanje, brisanje i filtriranje programa te
 * slanje zahtjeva za produženje programa.
 */
public class ProgramsScreenController {
    @FXML TextField nameTextField;
    @FXML ComboBox<Sponsor> sponsorCB;
    @FXML DatePicker startDatePicker;
    @FXML DatePicker endDatePicker;
    @FXML TextArea descriptionTextArea;
    @FXML ComboBox<String> filterCB;
    @FXML TableView<Program> tableView;
    @FXML TableColumn<Program, String> programNameTC;
    @FXML TableColumn<Program, String> programDescriptionTC;
    @FXML TableColumn<Program, String> programSponsorTC;
    @FXML TableColumn<Program, String> programStartTC;
    @FXML TableColumn<Program, String> programEndTC;
    @FXML TableColumn<Program, Void> editColumn;
    @FXML TableColumn<Program, Void> deleteColumn;
    @FXML TableColumn<Program, Void> requestExtensionTC;
    @FXML Button addButton;
    @FXML Button saveButton;
    @FXML Label errorLabel;

    private ObservableList<Program> programsObsList;
    private Program editingProgram;
    private static final Logger logger = LoggerFactory.getLogger(ProgramsScreenController.class);

    /**
     * Inicijalizira kontroler: učitava programe i sponzore,
     * postavlja filtere, tablicu i raspoređuje periodično osvježavanje.
     */
    public void initialize() {
        programsObsList = FXCollections.observableList(DatabaseUtils.loadPrograms());
        sponsorCB.setItems(FXCollections.observableList(DatabaseUtils.loadSponsors()));
        filterCB.setItems(FXCollections.observableArrayList("Svi", "Aktivni", "Istekli"));
        filterCB.setValue("Svi");
        filterCB.setOnAction(e -> applyFilter());
        tableView.setItems(programsObsList);
        setupTable();
        new Timeline(new KeyFrame(Duration.seconds(10), e -> applyFilter())).play();
        saveButton.setVisible(false);
        errorLabel.setVisible(false);
    }

    /**
     * Postavlja stupce tablice i definira akcije za tipke u tablici.
     */
    private void setupTable() {
        programNameTC.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getName()));
        programDescriptionTC.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getDescription()));
        programSponsorTC.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getSponsor().getName()));
        programStartTC.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        programEndTC.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getEndDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        setupActionButtonColumn(editColumn, "Edit", this::loadProgramToForm);
        setupActionButtonColumn(deleteColumn, "Delete", this::deleteProgram);
        setupExtensionRequestColumn();
    }

    /**
     * Pomoćna metoda za postavljanje stupca s gumbom i definiranje akcije na klik gumba.
     *
     * @param col stupac u tablici
     * @param label tekst na gumbu
     * @param handler handler koji obrađuje akciju gumba
     */
    private void setupActionButtonColumn(TableColumn<Program, Void> col, String label, ProgramHandler handler) {
        col.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button(label);
            { btn.setOnAction(e -> handler.handle(getTableView().getItems().get(getIndex()))); }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    /**
     * Postavlja stupac za zahtjev za produženje s gumbom i pripadajućim dijalogom za unos.
     */
    private void setupExtensionRequestColumn() {
        requestExtensionTC.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button("Request Extension");

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Program program = getTableView().getItems().get(getIndex());
                btn.setOnAction(e -> {
                    TextField daysInput = new TextField();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Zahtjev za produženje");
                    alert.setHeaderText("Unesi broj dana:");
                    alert.getDialogPane().setContent(daysInput);
                    alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
                    if (alert.showAndWait().filter(b -> b == ButtonType.OK).isEmpty()) return;
                    try {
                        int days = Integer.parseInt(daysInput.getText());
                        String token = TokenGenerator.generateToken();
                        DatabaseUtils.requestProgramExtension(program.getId(), days, token);
                        ChangeManager.addNewChange(new Change("Zahtjev za produljenje " + program.getName(), Session.getSession().getCurrentUser().username(), "", ""));
                        MailSender.sendHtmlMail(program.getSponsor().getEmail(), "Zahtjev za produljenjem roka.",
                                EmailTemplateBuilder.buildExtensionEmail(program.getSponsor().getName(), program.getName(), token, days));
                    } catch (Exception ex) {
                        showErrorAlert("Neispravan unos: " + ex.getMessage());
                    }
                });
                setGraphic(btn);
            }
        });
    }

    /**
     * Dodaje novi program na temelju unesenih podataka.
     * Ako podaci nisu ispravni ili korisnik otkaže, operacija se ne izvršava.
     */
    public void addNewProgram() {
        if (!fieldCheck()) { errorLabel.setVisible(true); return; }
        if (!showConfirmation("Dodavanje programa", "Dodati novi program?")) return;
        try {
            DatabaseUtils.insertProgram(nameTextField.getText(), sponsorCB.getValue().getId(), 200.00,
                    descriptionTextArea.getText(), startDatePicker.getValue(), endDatePicker.getValue());
        } catch (ItemAlreadyExistsException e) {
            logger.error(e.getMessage());
            errorLabel.setText("A program with this name already exists.");
        }
        refreshTable(); clearData();
    }

    /**
     * Izvozi podatke o svim programima u CSV datoteku.
     */
    public void exportPrograms(){
        DataExportUtil<Program> dataExportUtil = new DataExportUtil<>();
        boolean success = dataExportUtil.exportToCSV(DatabaseUtils.loadPrograms(), "files/programs.csv");
        if(!success){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Neuspješan izvor u CSV datoteku.");
            alert.setHeaderText("Došlo je do pogreške pri izvozu podataka u CSV datoteku.");
            alert.getButtonTypes().setAll(ButtonType.OK);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uspješan izvoz");
            alert.setHeaderText("Podaci o programima su uspješno izvezeni u CSV datoteku programs.csv");
            alert.getButtonTypes().setAll(ButtonType.OK);
        }
    }

    /**
     * Sprema izmjene na trenutno uređivanom programu.
     * Ako nema odabranog programa ili podaci nisu valjani, operacija se ne izvršava.
     */
    public void saveEditedProgram() {
        if (editingProgram == null || !fieldCheck()) { errorLabel.setVisible(true); return; }
        if (!showConfirmation("Spremi izmjene", "Spremiti promjene?")) return;
        Program updated = new Program(editingProgram.getId(), nameTextField.getText(), sponsorCB.getValue(),
                editingProgram.getDailyAmount(), descriptionTextArea.getText(),
                startDatePicker.getValue(), endDatePicker.getValue());
        DatabaseUtils.updateProgram(updated);
        ChangeManager.addNewChange(new Change("Uređen program " + editingProgram.getName(), Session.getSession().getCurrentUser().username(), editingProgram.toString(), updated.toString()));
        refreshTable(); clearEditMode();
    }

    /**
     * Briše zadani program nakon potvrde korisnika.
     *
     * @param program program koji se briše
     */
    private void deleteProgram(Program program) {
        if (!showConfirmation("Brisanje programa", "Izbrisati program?")) return;
        DatabaseUtils.deleteById("programs", program.getId());
        ChangeManager.addNewChange(new Change("Izbrisan program " + program.getName(), Session.getSession().getCurrentUser().username(), program.getName(), ""));
        programsObsList.remove(program);
        if (program.equals(editingProgram)) clearEditMode();
    }

    /**
     * Primjenjuje odabrani filter na listu programa (Svi, Aktivni, Istekli).
     */
    private void applyFilter() {
        String selected = filterCB.getValue();
        var allPrograms = FXCollections.observableList(DatabaseUtils.loadPrograms());
        ObservableList<Program> filtered = switch (selected) {
            case "Aktivni" -> allPrograms.filtered(p -> !p.getEndDate().isBefore(java.time.LocalDate.now()));
            case "Istekli" -> allPrograms.filtered(p -> p.getEndDate().isBefore(java.time.LocalDate.now()));
            default -> allPrograms;
        };
        programsObsList.setAll(filtered);
        tableView.refresh();
    }

    /**
     * Učitava podatke iz odabranog programa u formu za uređivanje.
     *
     * @param p program koji se uređuje
     */
    private void loadProgramToForm(Program p) {
        editingProgram = p;
        nameTextField.setText(p.getName());
        sponsorCB.setValue(p.getSponsor());
        startDatePicker.setValue(p.getStartDate());
        endDatePicker.setValue(p.getEndDate());
        descriptionTextArea.setText(p.getDescription());
        addButton.setVisible(false);
        saveButton.setVisible(true);
        errorLabel.setVisible(false);
    }

    /**
     * Provjerava jesu li potrebna polja ispravno popunjena.
     *
     * @return true ako su sva polja popunjena, false inače
     */
    private boolean fieldCheck() {
        return Stream.of(nameTextField.getText(), descriptionTextArea.getText()).noneMatch(String::isEmpty)
                && sponsorCB.getValue() != null && startDatePicker.getValue() != null && endDatePicker.getValue() != null;
    }

    /**
     * Prikazuje potvrdu dijalog korisniku.
     *
     * @param title naslov dijaloga
     * @param msg poruka dijaloga
     * @return true ako je korisnik potvrdio, false inače
     */
    private boolean showConfirmation(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title); alert.setHeaderText(null);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    /**
     * Prikazuje alert s greškom korisniku.
     *
     * @param msg poruka greške
     */
    private void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Greška"); alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Čisti podatke u formi.
     */
    private void clearData() {
        nameTextField.clear(); sponsorCB.setValue(null);
        startDatePicker.setValue(null); endDatePicker.setValue(null);
        descriptionTextArea.clear(); errorLabel.setVisible(false);
    }

    /**
     * Resetira formu i prekida režim uređivanja.
     */
    private void clearEditMode() {
        editingProgram = null; clearData();
        addButton.setVisible(true); saveButton.setVisible(false);
    }

    /**
     * Osvježava prikaz tablice s programima.
     */
    private void refreshTable() {
        programsObsList.setAll(DatabaseUtils.loadPrograms());
        tableView.refresh();
    }

    /**
     * Funkcijski interface za rukovanje programom (npr. edit ili delete).
     */
    @FunctionalInterface interface ProgramHandler { void handle(Program p); }
}
