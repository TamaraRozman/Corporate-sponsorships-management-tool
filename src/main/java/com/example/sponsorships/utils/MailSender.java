package com.example.sponsorships.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Pomoćna klasa za slanje HTML e-mail poruka putem SMTP protokola.
 * <p>
 * Klasa koristi konfiguraciju iz datoteke <code>conf/mail.properties</code> za autentikaciju
 * i slanje poruka koristeći Gmail SMTP server.
 * </p>
 *
 * <p>Klasa ne može biti instancirana jer sadrži privatni konstruktor.</p>
 */
public class MailSender {

    /**
     * Privatni konstruktor kako bi se onemogućilo instanciranje klase.
     */
    private MailSender() {}

    /**
     * Šalje HTML e-mail poruku na zadanu adresu.
     *
     * @param to           e-mail adresa primatelja
     * @param subject      predmet poruke
     * @param htmlContent  HTML sadržaj poruke
     * @throws MessagingException ako dođe do greške pri slanju poruke
     * @throws IOException        ako dođe do greške pri čitanju konfiguracijske datoteke
     */
    public static void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException, IOException {
        Properties svojstva = new Properties();

        try (FileReader reader = new FileReader("conf/mail.properties")) {
            svojstva.load(reader);
        }

        String username = svojstva.getProperty("mail.username");
        String password = svojstva.getProperty("mail.password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        jakarta.mail.Session session = jakarta.mail.Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(msg);
    }
}
