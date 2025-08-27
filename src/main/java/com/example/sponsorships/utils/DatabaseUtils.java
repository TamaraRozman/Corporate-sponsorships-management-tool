package com.example.sponsorships.utils;

import com.example.sponsorships.entities.*;
import com.example.sponsorships.enums.CITY;
import com.example.sponsorships.exceptions.ItemAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class DatabaseUtils {
    private DatabaseUtils() {}

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final Map<Long, Address> addressesMap = new HashMap<>();
    private static final String DATABASE_FILE = "conf/database.properties";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String DATE_OF_BIRTH = "date_of_birth";

    @FunctionalInterface
    interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
    @FunctionalInterface
    interface SQLFunction<T, R> {
        R apply(T t) throws SQLException;
    }
    public static Connection connectToDatabase() throws SQLException, IOException {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(DATABASE_FILE)) {
            props.load(reader);
        }
        return DriverManager.getConnection(props.getProperty("databaseURL"), props.getProperty("username"), props.getProperty("password"));
    }
    private static <R> void executeQuery(String sql, SQLFunction<PreparedStatement, R> function, SQLConsumer<PreparedStatement> paramSetter) {
        try (Connection conn = connectToDatabase(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (paramSetter != null) paramSetter.accept(ps);
            function.apply(ps);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    private static int executeUpdate(String sql, SQLConsumer<PreparedStatement> paramSetter, boolean returnGeneratedKey) {
        try (Connection conn = connectToDatabase(); PreparedStatement ps = conn.prepareStatement(sql, returnGeneratedKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
            paramSetter.accept(ps);
            ps.executeUpdate();
            if (returnGeneratedKey) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }
    public static void deleteById(String tableName, Long id) {
        String sql = "DELETE FROM " + tableName + " WHERE ID = ?";
        try (Connection conn = connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Error deleting from {}: {}", tableName, e.getMessage());
        }
    }
    private static void loadAddresses() {
        String sql = "SELECT id, street_name, house_number, city FROM Addresses";
        executeQuery(sql, ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Address address = new AddressBuilder()
                            .setId(rs.getLong("id"))
                            .setStreetName(rs.getString("street_name"))
                            .setHouseNumber(rs.getString("house_number"))
                            .setCity(CITY.getCityByName(rs.getString("city"))).createAddress();
                    addressesMap.put(address.getId(), address);
                }
            }
            return null;
        }, null);
    }
    public static Address getAddressById(Long id) {
        if (addressesMap.isEmpty()) loadAddresses();
        return addressesMap.get(id);
    }
    public static int addAddress(String city, String street, String house) {
        String sql = "INSERT INTO ADDRESSES (city, street_name, house_number) VALUES (?, ?, ?)";
        return executeUpdate(sql, ps -> {
            ps.setString(1, city);
            ps.setString(2, street);
            ps.setString(3, house);
            ChangeManager.addNewChange(new Change("Dodana nova adresa.", Session.getSession().getCurrentUser().username(), "", street + " " + house + ", " + city));
        }, true);
    }
    public static void updateAddress(long id, String city, String street, String house) {
        String sql = "UPDATE ADDRESSES SET city = ?, street_name = ?, house_number = ? WHERE id = ?";
        executeUpdate(sql, ps -> {
            ps.setString(1, city);
            ps.setString(2, street);
            ps.setString(3, house);
            ps.setLong(4, id);
        }, false);
    }
    public static int addPerson(String firstName, String lastName, LocalDate dob, String city, String street, String house) throws SQLException, IOException {
        try (Connection conn = connectToDatabase()) {
            conn.setAutoCommit(false);
            int addressId = addAddress(city, street, house);
            String sql = "INSERT INTO Persons (first_name, last_name, date_of_birth, address_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setDate(3, Date.valueOf(dob));
                ps.setInt(4, addressId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        conn.commit();
                        ChangeManager.addNewChange(new Change("Dodana nova osoba.", Session.getSession().getCurrentUser().username(), "", firstName + " " + lastName));
                        return keys.getInt(1);
                    }
                }
            }
        }
        return 0;
    }
    public static List<Sponsor> loadSponsors() {
        List<Sponsor> sponsors = new ArrayList<>();

        String sql = """
        SELECT s.id AS sponsor_id, s.name AS sponsor_name, s.email,
               sa.id AS sponsor_address_id, sa.street_name AS sponsor_street, sa.house_number AS sponsor_house, sa.city AS sponsor_city,
               p.id AS contact_person_id, p.first_name, p.last_name, p.date_of_birth,
               pa.id AS contact_address_id, pa.street_name AS contact_street, pa.house_number AS contact_house, pa.city AS contact_city
        FROM Sponsors s
        JOIN Addresses sa ON s.address_id = sa.id
        JOIN Persons p ON s.contact_person_id = p.id
        JOIN Addresses pa ON p.address_id = pa.id
    """;

        try (Connection connection = connectToDatabase();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Long sponsorId = rs.getLong("sponsor_id");
                String sponsorName = rs.getString("sponsor_name");
                String email = rs.getString("email");

                Long sponsorAddressId = rs.getLong("sponsor_address_id");
                String sponsorStreet = rs.getString("sponsor_street");
                String sponsorHouse = rs.getString("sponsor_house");
                CITY sponsorCity = CITY.getCityByName(rs.getString("sponsor_city"));
                Address sponsorAddress = new Address(sponsorAddressId, sponsorStreet, sponsorHouse, sponsorCity);

                Long contactPersonId = rs.getLong("contact_person_id");
                String firstName = rs.getString(FIRST_NAME);
                String lastName = rs.getString(LAST_NAME);
                LocalDate dateOfBirth = rs.getDate(DATE_OF_BIRTH).toLocalDate();

                Long contactAddressId = rs.getLong("contact_address_id");
                String contactStreet = rs.getString("contact_street");
                String contactHouse = rs.getString("contact_house");
                CITY contactCity = CITY.getCityByName(rs.getString("contact_city"));
                Address contactAddress = new Address(contactAddressId, contactStreet, contactHouse, contactCity);

                Person contactPerson = new Person(contactPersonId, firstName, lastName, dateOfBirth, contactAddress);

                Sponsor sponsor = new Sponsor(sponsorId, sponsorName, email, sponsorAddress, contactPerson);
                sponsors.add(sponsor);
            }

        } catch (SQLException | IOException e) {
            logger.error("Error loading sponsors: {}", e.getMessage());
        }

        return sponsors;
    }
    public static Sponsor getSponsorById(Long id) throws SQLException, IOException {
        String sql = """
        SELECT s.id AS sponsor_id, s.name AS sponsor_name, s.email,
               sa.id AS address_id, sa.street_name AS street_name, sa.house_number AS house_number, sa.city AS city,
               p.id AS contact_person_id, p.first_name, p.last_name, p.date_of_birth,
               pa.id AS contact_address_id, pa.street_name AS contact_street, pa.house_number AS contact_house, pa.city AS contact_city
        FROM Sponsors s
        JOIN Addresses sa ON s.address_id = sa.id
        JOIN Persons p ON s.contact_person_id = p.id
        JOIN Addresses pa ON p.address_id = pa.id
        WHERE s.id= ?
    """;

        try (Connection connection = connectToDatabase(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("No sponsor found with ID: " + id);
            }

            Long sponsorId = rs.getLong("id");
            String sponsorName = rs.getString("name");
            String email = rs.getString("email");
            if (sponsorName == null || email == null) {
                throw new SQLException("Required sponsor information is missing.");
            }

            Long sponsorAddressId = rs.getLong("address_id");
            String sponsorStreet = rs.getString("street_name");
            String sponsorHouse = rs.getString("house_number");
            String sponsorCity = rs.getString("city");
            if (sponsorStreet == null || sponsorHouse == null || sponsorCity == null) {
                throw new SQLException("Address details are incomplete.");
            }
            Address sponsorAddress = getAddressById(sponsorAddressId);

            Long contactPersonId = rs.getLong("contact_person_id");
            String firstName = rs.getString(FIRST_NAME);
            String lastName = rs.getString(LAST_NAME);
            LocalDate dateOfBirth = rs.getDate(DATE_OF_BIRTH) != null ? rs.getDate(DATE_OF_BIRTH).toLocalDate() : null;
            if (firstName == null || lastName == null || dateOfBirth == null) {
                throw new SQLException("Contact person details are incomplete.");
            }

            Long contactAddressId = rs.getLong("contact_address_id");
            String contactStreet = rs.getString("contact_street");
            String contactHouse = rs.getString("contact_house");
            String contactCity = rs.getString("contact_city");
            if (contactStreet == null || contactHouse == null || contactCity == null) {
                throw new SQLException("Contact address details are incomplete.");
            }
            Address contactAddress = new Address(contactAddressId, contactStreet, contactHouse, CITY.getCityByName(contactCity));

            Person contactPerson = new Person(contactPersonId, firstName, lastName, dateOfBirth, contactAddress);

            return new Sponsor(sponsorId, sponsorName, email, sponsorAddress, contactPerson);
        }
    }
    public static void updatePerson(long id, String firstName, String lastName, LocalDate dob, long addressId) {
        String sql = "UPDATE Persons SET first_name = ?, last_name = ?, date_of_birth = ?, address_id = ? WHERE id = ?";
        executeUpdate(sql, ps -> {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setDate(3, Date.valueOf(dob));
            ps.setLong(4, addressId);
            ps.setLong(5, id);
        }, false);
    }
    public static int addSponsor(String sponsorName, String email, Person person, Address contactAddress, String sponsorCity, String sponsorStreet, String sponsorHouse) {
        try (Connection conn = connectToDatabase()) {
            conn.setAutoCommit(false);
            int sponsorAddressId = addAddress(sponsorCity, sponsorStreet, sponsorHouse);
            int contactPersonId = addPerson(person.getName(), person.getSurname(), person.getDateOfBirth(), contactAddress.getCity().getCityName(), contactAddress.getStreetName(), contactAddress.getHouseNumber());
            String sql = "INSERT INTO Sponsors (name, email, address_id, contact_person_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sponsorName);
                ps.setString(2, email);
                ps.setInt(3, sponsorAddressId);
                ps.setInt(4, contactPersonId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        conn.commit();
                        ChangeManager.addNewChange(new Change("Dodana novi sponzor.", Session.getSession().getCurrentUser().username(), "", sponsorName));
                        return keys.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error adding sponsor: {}", e.getMessage());
        }
        return 0;
    }
    public static void updateSponsor(Sponsor sponsor) {
        Address sponsorAddress = sponsor.getAddress();
        updateAddress(sponsorAddress.getId(), sponsorAddress.getCity().getCityName(),
                sponsorAddress.getStreetName(), sponsorAddress.getHouseNumber());

        Person contactPerson = sponsor.getContactPerson();
        Address personAddress = contactPerson.getAddress();
        updateAddress(personAddress.getId(), personAddress.getCity().getCityName(),
                personAddress.getStreetName(), personAddress.getHouseNumber());

        updatePerson(contactPerson.getId(), contactPerson.getName(),
                contactPerson.getSurname(), contactPerson.getDateOfBirth(),
                personAddress.getId());

        String sql = "UPDATE Sponsors SET name = ?, email = ? WHERE id = ?";
        executeUpdate(sql, ps -> {
            ps.setString(1, sponsor.getName());
            ps.setString(2, sponsor.getEmail());
            ps.setLong(3, sponsor.getId());
        }, false);
    }
    public static void insertProgram(String name, long sponsorId, double dailyAmount, String description, LocalDate startDate, LocalDate endDate) throws ItemAlreadyExistsException {
        // Prvo provjeri postoji li program s istim imenom
        if (programExists(name)) {
            throw new ItemAlreadyExistsException("Program s nazivom '" + name + "' već postoji.");
        }

        String sql = "INSERT INTO Programs (name, sponsor_id, daily_amount, description, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?)";
        executeUpdate(sql, ps -> {
            ps.setString(1, name);
            ps.setLong(2, sponsorId);
            ps.setDouble(3, dailyAmount);
            ps.setString(4, description);
            ps.setDate(5, Date.valueOf(startDate));
            ps.setDate(6, Date.valueOf(endDate));
        }, true);
    }
    private static boolean programExists(String name) {
        String sql = "SELECT 1 FROM Programs WHERE name = ?";
        try (Connection conn = connectToDatabase();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public static List<Program> loadPrograms() {
        List<Program> programs = new ArrayList<>();
        String sql = "SELECT id, name, sponsor_id, daily_amount, description, start_date, end_date FROM Programs";

        try (Connection connection = connectToDatabase();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                Long sponsorId = rs.getLong("sponsor_id");
                Long dailyAmount = rs.getLong("daily_amount");
                String description = rs.getString("description");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();

                Sponsor sponsor = getSponsorById(sponsorId);
                programs.add(new Program(id, name, sponsor, dailyAmount, description, startDate, endDate));
            }

        } catch (SQLException | IOException e) {
            logger.error("Error loading programs: {}", e.getMessage());
        }

        return programs;
    }
    public static void updateProgram(Program program) {
        String sql = "UPDATE Programs SET name = ?, sponsor_id = ?, daily_amount = ?, description = ?, start_date = ?, end_date = ? WHERE id = ?";
        executeUpdate(sql, ps -> {
            ps.setString(1, program.getName());
            ps.setLong(2, program.getSponsor().getId());
            ps.setDouble(3, program.getDailyAmount());
            ps.setString(4, program.getDescription());
            ps.setDate(5, java.sql.Date.valueOf(program.getStartDate()));
            ps.setDate(6, java.sql.Date.valueOf(program.getEndDate()));
            ps.setLong(7, program.getId());
        }, false);
    }
    public static void requestProgramExtension(Long programId, int daysRequested, String token) {
        String sql = "INSERT INTO ProgramExtensionRequests (program_id, days_requested, status, token) VALUES (?, ?, 'PENDING', ?)";
        executeUpdate(sql, ps -> {
            ps.setLong(1, programId);
            ps.setInt(2, daysRequested);
            ps.setString(3, token);
        }, false);
    }
    public static void updateProgramExtensionStatus(String token, String status) {
        String sql = "UPDATE PROGRAMEXTENSIONREQUESTS SET status = ? WHERE token = ?";
        executeUpdate(sql, ps -> {
            ps.setString(1, status);
            ps.setString(2, token);
        }, false);
    }
    public static void acceptExtensionRequest(String token) {
        String selectExtensionSql = "SELECT program_id, days_requested FROM ProgramExtensionRequests WHERE token = ?";
        String selectEndDateSql = "SELECT end_date FROM Programs WHERE id = ?";
        String updateEndDateSql = "UPDATE Programs SET end_date = ? WHERE id = ?";
        String updateStatusSql = "UPDATE ProgramExtensionRequests SET status = 'APPROVED' WHERE token = ?";

        try (Connection conn = connectToDatabase()) {
            conn.setAutoCommit(false);

            long programId = 0;
            int daysRequested = 0;

            try (PreparedStatement ps = conn.prepareStatement(selectExtensionSql)) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        programId = rs.getLong("program_id");
                        daysRequested = rs.getInt("days_requested");
                    } else {
                        logger.warn("Zahtjev za produženje nije pronađen za token: {}", token);
                        return;
                    }
                }
            }

            LocalDate endDate;
            try (PreparedStatement ps = conn.prepareStatement(selectEndDateSql)) {
                ps.setLong(1, programId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        endDate = rs.getDate("end_date").toLocalDate();
                    } else {
                        logger.warn("Program nije pronađen za ID: {}", programId);
                        return;
                    }
                }
            }
            LocalDate newEndDate = endDate.plusDays(daysRequested);
            try (PreparedStatement ps = conn.prepareStatement(updateEndDateSql)) {
                ps.setDate(1, java.sql.Date.valueOf(newEndDate));
                ps.setLong(2, programId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(updateStatusSql)) {
                ps.setString(1, token);
                ps.executeUpdate();
            }
            conn.commit();
            ChangeManager.addNewChange(new Change(
                    "Zahtjev za produženje prihvaćen.",
                    Session.getSession().getCurrentUser().username(),
                    endDate.toString(),
                    newEndDate.toString()
            ));
        } catch (Exception e) {
            logger.error("Greška prilikom prihvaćanja zahtjeva za produženje: {}", e.getMessage());
        }
    }
    public static void denyExtensionRequest(String token) { updateProgramExtensionStatus(token, "REJECTED"); }
}
