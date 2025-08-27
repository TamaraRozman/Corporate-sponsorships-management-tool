package com.example.sponsorships.utils;

/**
 * Pomoćna klasa za generiranje HTML predložaka e-mail poruka.
 * <p>
 * Klasa je nenasiljiva i koristi se isključivo putem statičkih metoda.
 */
public class EmailTemplateBuilder {

    /** Privatni konstruktor da se spriječi instanciranje ove klase. */
    private EmailTemplateBuilder() {
    }

    /**
     * Generira HTML predložak e-maila za traženje produženja programa.
     *
     * @param sponsorName   ime sponzora kojem se e-mail šalje
     * @param programName   naziv programa za koji se traži produženje
     * @param token         sigurnosni token koji se koristi za potvrdu ili odbijanje zahtjeva
     * @param daysRequested broj dana za koje se traži produženje
     * @return HTML string koji predstavlja tijelo e-mail poruke
     */
    public static String buildExtensionEmail(String sponsorName, String programName, String token, int daysRequested) {
        String baseUrl = "http://localhost:8080/extension-response";

        return String.format("""
        <html>
            <body style="font-family:Arial,sans-serif; line-height:1.6">
                <h2>Poštovani %s,</h2>
                <p>Za program <strong>%s</strong> zatraženo je produženje roka od <strong>%d radnih dana</strong>.</p>
                <p>Molimo Vas da odgovorite klikom na jednu od opcija:</p>
                <a href="%s?action=accept&token=%s"
                   style="padding:10px 15px; background-color:#28a745; color:white; text-decoration:none; border-radius:5px;">
                   ✅ Prihvati
                </a>
                &nbsp;
                <a href="%s?action=deny&token=%s"
                   style="padding:10px 15px; background-color:#dc3545; color:white; text-decoration:none; border-radius:5px;">
                   ❌ Odbij
                </a>
                <p>Hvala,<br>Vaš tim</p>
            </body>
        </html>
        """, sponsorName, programName, daysRequested, baseUrl, token, baseUrl, token);
    }
}
