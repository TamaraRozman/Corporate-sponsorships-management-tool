package com.example.sponsorships.controllers;

import com.example.sponsorships.entities.ChartDataAdapter;
import com.example.sponsorships.entities.Program;
import com.example.sponsorships.entities.Sponsor;
import com.example.sponsorships.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kontroler za ekran statistike koji upravlja prikazom grafikona ulaganja po sponzorima
 * i zahtjeva za ekstenziju programa.
 */
public class StatisticsScreenController {

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis extensionXAxis;

    @FXML
    private NumberAxis extensionYAxis;


    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private BarChart<String, Number> extensionChart;

    /**
     * Inicijalizacijska metoda koja se poziva nakon učitavanja FXML-a.
     * Učitava programe i sponzore, priprema podatke za glavni bar chart (ulaganja po sponzoru)
     * i poziva metodu za učitavanje dodatnog grafa sa zahtjevima za ekstenziju.
     */
    @FXML
    public void initialize() {
        List<Program> programs = DatabaseUtils.loadPrograms();
        List<Sponsor> sponsors = DatabaseUtils.loadSponsors();

        ChartDataAdapter<Sponsor, XYChart.Data<String, Number>> adapter = new ChartDataAdapter<>(sponsor -> {
            long totalInvestment = programs.stream()
                    .filter(p -> p.getSponsor().equals(sponsor))
                    .mapToLong(Program::getFullAmount)
                    .sum();
            return new XYChart.Data<>(sponsor.getName(), totalInvestment);
        });

        List<XYChart.Data<String, Number>> chartData = adapter.adapt(sponsors);
        loadExtensionChart();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ukupno ulaganje/€");
        series.getData().addAll(chartData);

        barChart.getData().clear();
        barChart.getData().add(series);
        barChart.setTitle("Ulaganja po sponzoru");
    }

    /**
     * Puni dodatni bar chart podacima o broju zahtjeva za ekstenziju za svaki program.
     * Dohvaća podatke iz baze, broji zahtjeve za svaki program i prikazuje ih u grafu.
     */
    private void loadExtensionChart() {
        List<Program> allPrograms = DatabaseUtils.loadPrograms();
        Map<Long, Program> programMap = allPrograms.stream()
                .collect(Collectors.toMap(Program::getId, p -> p));

        Map<Program, Long> requestCountPerProgram = new HashMap<>();

        String sql = "SELECT program_id FROM ProgramExtensionRequests";

        try (Connection conn = DatabaseUtils.connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long programId = rs.getLong("program_id");
                Program program = programMap.get(programId);
                if (program != null) {
                    requestCountPerProgram.put(program, requestCountPerProgram.getOrDefault(program, 0L) + 1);
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return;
        }

        XYChart.Series<String, Number> extensionSeries = new XYChart.Series<>();
        extensionSeries.setName("Broj zahtjeva za ekstenziju");

        for (Map.Entry<Program, Long> entry : requestCountPerProgram.entrySet()) {
            extensionSeries.getData().add(
                    new XYChart.Data<>(entry.getKey().getName(), entry.getValue())
            );
        }

        extensionChart.getData().add(extensionSeries);
        extensionChart.setTitle("Zahtjevi za ekstenziju po programu");
        extensionXAxis.setLabel("Programi");
        extensionYAxis.setLabel("Broj zahtjeva");
    }

}
