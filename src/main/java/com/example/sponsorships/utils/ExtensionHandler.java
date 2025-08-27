package com.example.sponsorships.utils;

import com.example.sponsorships.entities.Change;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler klasa za obradu HTTP zahtjeva vezanih za produženje programa.
 * <p>
 * Ova klasa se koristi u jednostavnom HTTP serveru za prihvaćanje ili odbijanje zahtjeva za produženje programa
 * na temelju pristiglog URL tokena i akcije.
 */
public class ExtensionHandler implements HttpHandler {

    /**
     * Obrada HTTP zahtjeva koji uključuju akcije "accept" ili "deny" vezane uz zahtjev za produženje programa.
     *
     * @param exchange objekt koji predstavlja dolazni HTTP zahtjev i omogućuje slanje odgovora
     * @throws IOException u slučaju problema sa slanjem odgovora
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final Logger logger = LoggerFactory.getLogger(ExtensionHandler.class);

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);

        String action = params.get("action");
        String token = params.get("token");

        logger.info("Received action: {}", action);
        logger.info("Received token: {}", token);

        String responseText;
        if (action != null && token != null) {
            switch (action) {
                case "accept":
                    responseText = "Extension approved successfully!";
                    DatabaseUtils.acceptExtensionRequest(token);
                    ChangeManager.addNewChange(new Change("Extension request approved.",
                            Session.getSession().getCurrentUser().username(), "PENDING", "APPROVED"));
                    break;
                case "deny":
                    responseText = "Extension denied.";
                    DatabaseUtils.denyExtensionRequest(token);
                    ChangeManager.addNewChange(new Change("Extension request approved.",
                            Session.getSession().getCurrentUser().username(), "PENDING", "REJECTED"));
                    break;
                default:
                    responseText = "Unknown action.";
                    break;
            }
        } else {
            responseText = "Missing parameters.";
        }

        exchange.sendResponseHeaders(200, responseText.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseText.getBytes());
        }
    }

    /**
     * Parsira parametre iz URL query stringa u mapu ključ–vrijednost.
     *
     * @param query query string iz URL-a (npr. action=accept&token=abc123)
     * @return mapa koja sadrži ključ–vrijednost parove iz query stringa
     */
    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) return Map.of();
        return java.util.Arrays.stream(query.split("&"))
                .map(s -> s.split("="))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr[1]
                ));
    }
}
