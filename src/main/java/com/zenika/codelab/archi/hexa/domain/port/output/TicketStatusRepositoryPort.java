package com.zenika.codelab.archi.hexa.domain.port.output;

import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;

public interface TicketStatusRepositoryPort {

    TicketStatus getStatus(String code) throws DataNotFound;
}
