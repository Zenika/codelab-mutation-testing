package com.zenika.codelab.archi.hexa.domain.model;

import lombok.Getter;

@Getter
public enum TicketStatus {

    FACTURE("F", "Facture"),
    AVOIR("A", "Avoir"),
    RETOUR("R", "Retour"),
    GARANTIE("G", "Garantie"),
    TEST("T", "Test"),
    UNKNOW("U", "N/A");

    private final String code;

    private final String libelle;

    TicketStatus(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }
}
