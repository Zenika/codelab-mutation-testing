package com.zenika.codelab.archi.hexa.domain.port.input;

import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;

public interface TicketStatusPort {

    TicketStatus getStatus(String code);
}
