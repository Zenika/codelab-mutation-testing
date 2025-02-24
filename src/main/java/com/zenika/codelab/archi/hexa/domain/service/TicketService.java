package com.zenika.codelab.archi.hexa.domain.service;

import com.zenika.codelab.archi.hexa.domain.exception.DataQualityException;
import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;
import com.zenika.codelab.archi.hexa.domain.port.input.TicketServicePort;
import com.zenika.codelab.archi.hexa.domain.port.output.TicketServiceRepositoryPort;
import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TicketService implements TicketServicePort {
    private TicketServiceRepositoryPort repository;

    @Override
    public List<TicketVO> getAllTicketFromStartDate(LocalDate date) {
        return repository.findTicketAfterDate(date);
    }

    @Override
    public List<TicketVO> getAllTicketFromStartDateAndCaisse(LocalDate date, String idCaisse) {
        return repository.findAllTicketFromStartDateAndCaisse(date, idCaisse);
    }

    @Override
    public TicketVO getTicketFromId(Long id) throws DataQualityException, DataNotFound {
        var t = repository.getFromId(id);

        log.debug("Entity {} ", t);
        // compute the montant
        if (controlMontantFacture(t)) {
            return t;
        } else throw new DataQualityException(t);
    }


    private boolean controlMontantFacture(TicketVO ticket) {

        var montantSigne = Optional.ofNullable(ticket.montantTotal()).orElse(BigDecimal.ZERO).signum();
        var codeStatus = ticket.ticketStatus().getCode();
        if(montantSigne != 0){
            if (montantSigne ==-1) {
                return !codeStatus.equals(TicketStatus.FACTURE.getCode());
            }else {
                return !codeStatus.equals(TicketStatus.AVOIR.getCode());
            }
        }else {
            return true;
        }
    }

}
