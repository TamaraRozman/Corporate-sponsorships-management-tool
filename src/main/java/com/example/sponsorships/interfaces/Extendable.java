package com.example.sponsorships.interfaces;

import com.example.sponsorships.entities.Program;
/**
 * Sučelje koje označava da objekt ima mogućnost produženja trajanja.
 * Implementirajuće klase moraju implementirati metodu {@code extendDueDateBy(int days)}
 * koja produžuje datum završetka za zadani broj dana.
 *
 * Zapečaćeno je (sealed) tako da ga može implementirati samo klasa {@link Program}.
 */
public sealed interface Extendable permits Program {
    void extendDueDateBy(int days);
}
