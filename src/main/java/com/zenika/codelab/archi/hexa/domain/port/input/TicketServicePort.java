package com.zenika.codelab.archi.hexa.domain.port.input;

import com.zenika.codelab.archi.hexa.domain.exception.DataQualityException;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;

import java.time.LocalDate;
import java.util.List;

public interface TicketServicePort {
    List<TicketVO> getAllTicketFromStartDate(LocalDate date);

    List<TicketVO> getAllTicketFromStartDateAndCaisse(LocalDate date, String idCaisse);

    TicketVO getTicketFromId(Long id) throws DataQualityException;
}
