package com.zenika.codelab.archi.hexa.domain.port.output;

import com.zenika.codelab.archi.hexa.domain.model.TicketVO;

import java.time.LocalDate;
import java.util.List;

public interface TicketServiceRepositoryPort {

    TicketVO getFromId(Long id);

    List<TicketVO> findTicketAfterDate(LocalDate date);

    List<TicketVO> findAllTicketFromStartDateAndCaisse(LocalDate date, String libelleCaisse);
}
