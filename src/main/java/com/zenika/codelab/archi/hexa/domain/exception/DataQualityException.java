package com.zenika.codelab.archi.hexa.domain.exception;

import com.zenika.codelab.archi.hexa.domain.model.TicketVO;

public class DataQualityException extends Exception {
    public DataQualityException(TicketVO t) {
        super(String.format("Echec controle du model %s , montant %s ", t.ticketStatus().getLibelle(), t.montantTotal()));
    }
}
