package com.zenika.codelab.archi.hexa.domain.exception;

import com.zenika.codelab.archi.hexa.domain.model.TicketVO;

public class DataQualityException extends Exception {
    public DataQualityException(TicketVO t) {
        super(String.format("Echec controle du model %s id %d, montant %,.2f ", t.ticketStatus().getLibelle(), t.id(), t.montantTotal()));
    }
}
