package com.zenika.codelab.archi.hexa.domain.service;

import com.zenika.codelab.archi.hexa.domain.model.*;
import com.zenika.codelab.archi.hexa.domain.port.input.CalculCAPort;
import com.zenika.codelab.archi.hexa.domain.port.input.TicketServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

@Service
@AllArgsConstructor
public class CalculCAPortService implements CalculCAPort {

    private TicketServicePort ticketService;

    private static boolean filtreTicketStatus(TicketVO ticketVO) {
        var status = ticketVO.ticketStatus();

        return TicketStatus.FACTURE.getCode().equals(status.getCode()) || TicketStatus.AVOIR.getCode().equals(status.getCode());
    }

    private static double calculMontant(TicketVO t) {
        var montantInitial = t.montantTotal().doubleValue();
        if (TicketStatus.FACTURE.getCode().equals(t.ticketStatus().getCode())) {
            return montantInitial;
        } else {
            return montantInitial * -1;
        }

    }

    @Override
    public ChiffreAffaireGlobal computeFromDate(LocalDate date) {
        var tickets = ticketService.getAllTicketFromStartDate(date);
        var montantTotalParCaisse = tickets.stream().filter(CalculCAPortService::filtreTicketStatus).collect(groupingBy(TicketVO::caisse, summingDouble(CalculCAPortService::calculMontant)));

        var montantCAPositif = tickets.stream().filter(t -> TicketStatus.FACTURE.getCode().equals(t.ticketStatus().getCode())).map(TicketVO::montantTotal).mapToDouble(BigDecimal::doubleValue).sum();
        var montantNEgatif = tickets.stream().filter(t -> TicketStatus.AVOIR.getCode().equals(t.ticketStatus().getCode())).map(TicketVO::montantTotal).mapToDouble(BigDecimal::doubleValue).sum();

        var listCAPafCaisse = montantTotalParCaisse.keySet().stream().map(caisseVO -> new ChiffreAffaireParCaisse(caisseVO, BigDecimal.valueOf(montantTotalParCaisse.get(caisseVO)))).toList();
        return new ChiffreAffaireGlobal(listCAPafCaisse, BigDecimal.valueOf(montantCAPositif - montantNEgatif));
    }

    @Override
    public ChiffreAffaireParCaisse computeFromDateForCaisse(LocalDate date, String libelleCaisse) {
        var tickets = ticketService.getAllTicketFromStartDateAndCaisse(date, libelleCaisse);
        var montantTotalParCaisse = tickets.stream().filter(CalculCAPortService::filtreTicketStatus).collect(groupingBy(TicketVO::caisse, summingDouble(CalculCAPortService::calculMontant)));

        return montantTotalParCaisse.keySet().stream().findFirst().map(caisseVO -> new ChiffreAffaireParCaisse(caisseVO, BigDecimal.valueOf(montantTotalParCaisse.get(caisseVO)))).orElse(new ChiffreAffaireParCaisse(new CaisseVO(libelleCaisse), BigDecimal.ZERO));
    }
}
