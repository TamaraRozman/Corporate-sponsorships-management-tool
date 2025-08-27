module com.example.sponsorships {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;
    requires jakarta.mail;
    requires jdk.httpserver;

    opens com.example.sponsorships to javafx.fxml;
    exports com.example.sponsorships.main;
    opens com.example.sponsorships.main to javafx.fxml;
    exports com.example.sponsorships.controllers;
    opens com.example.sponsorships.controllers to javafx.fxml;
}