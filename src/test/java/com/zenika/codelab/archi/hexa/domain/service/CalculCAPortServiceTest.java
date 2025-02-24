package com.zenika.codelab.archi.hexa.domain.service;

import com.zenika.codelab.archi.hexa.domain.model.ArticleVO;
import com.zenika.codelab.archi.hexa.domain.model.CaisseVO;
import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;
import com.zenika.codelab.archi.hexa.domain.port.input.TicketServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculCAPortServiceTest {

    List<ArticleVO> articleVOS = List.of(new ArticleVO(1L, BigDecimal.valueOf(10), BigDecimal.valueOf(2), "Pizza", BigDecimal.valueOf(5)),
            new ArticleVO(1L, BigDecimal.valueOf(10), BigDecimal.valueOf(1), "Poulet", BigDecimal.valueOf(10)));
    List<TicketVO> ticketVOS = List.of(new TicketVO(1L, LocalDate.now(), articleVOS, new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(20)),
            new TicketVO(1L, LocalDate.now(), null, new CaisseVO("Caisse A"), TicketStatus.AVOIR, BigDecimal.valueOf(10)),
            new TicketVO(1L, LocalDate.now(), articleVOS, new CaisseVO("Caisse B"), TicketStatus.FACTURE, BigDecimal.valueOf(20)),
            new TicketVO(1L, LocalDate.now(), articleVOS, new CaisseVO("Caisse C"), TicketStatus.GARANTIE, BigDecimal.valueOf(20)));


    List<TicketVO> ticketVOSCaisseA = List.of(new TicketVO(1L, LocalDate.now(), articleVOS, new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(20)),
            new TicketVO(1L, LocalDate.now(), null, new CaisseVO("Caisse A"), TicketStatus.AVOIR, BigDecimal.valueOf(10)));

    List<TicketVO> ticketGarantie = List.of(new TicketVO(1L, LocalDate.now(), articleVOS, new CaisseVO("Caisse C"), TicketStatus.GARANTIE, BigDecimal.valueOf(20)));



    @InjectMocks
    private CalculCAPortService calculCAPortService;

    @Mock
    private TicketServicePort ticketService;

    @Test
    void computeFromDate() {
        when(ticketService.getAllTicketFromStartDate(any())).thenReturn(ticketVOS);
        var caGlobal = calculCAPortService.computeFromDate(LocalDate.now());
        assertThat(caGlobal).isNotNull();
        assertThat(caGlobal.caisses()).isNotNull().hasSize(2);
        //
        assertThat(caGlobal.caTotal().doubleValue()).isEqualTo(BigDecimal.valueOf(30.00).doubleValue());
        assertThat(caGlobal.caisses().get(0).caisseVO().libelle()).hasToString("Caisse A");
        assertThat(caGlobal.caisses().get(0).ca().doubleValue()).isEqualTo(BigDecimal.valueOf(10.00).doubleValue());
        assertThat(caGlobal.caisses().get(1).caisseVO().libelle()).hasToString("Caisse B");
        assertThat(caGlobal.caisses().get(1).ca().doubleValue()).isEqualTo(BigDecimal.valueOf(20.00).doubleValue());
    }

    @Test
    void computeFromDateForCaisse() {
        when(ticketService.getAllTicketFromStartDateAndCaisse(any(), any())).thenReturn(ticketVOSCaisseA);
        var caParCaisse = calculCAPortService.computeFromDateForCaisse(LocalDate.now(), "Caisse A");
        assertThat(caParCaisse).isNotNull();
        assertThat(caParCaisse.ca().doubleValue()).isEqualTo(10);
        assertThat(caParCaisse.caisseVO()).isNotNull();
        assertThat(caParCaisse.caisseVO().libelle()).isEqualTo("Caisse A");
    }

    @Test
    void computeFromDateForCaisse_return_default_value() {
        when(ticketService.getAllTicketFromStartDateAndCaisse(any(), any())).thenReturn(List.of());
        var caParCaisse = calculCAPortService.computeFromDateForCaisse(LocalDate.now(), "Caisse A");
        assertThat(caParCaisse).isNotNull();
        assertThat(caParCaisse.ca().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(caParCaisse.caisseVO()).isNotNull();
        assertThat(caParCaisse.caisseVO().libelle()).isEqualTo("Caisse A");
    }


    @Test
    void computeFromDateForCaisse_return_default_value_ticket_garantie() {
        when(ticketService.getAllTicketFromStartDateAndCaisse(any(), any())).thenReturn(ticketGarantie);
        var caParCaisse = calculCAPortService.computeFromDateForCaisse(LocalDate.now(), "Caisse A");
        assertThat(caParCaisse).isNotNull();
        assertThat(caParCaisse.ca().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(caParCaisse.caisseVO()).isNotNull();
        assertThat(caParCaisse.caisseVO().libelle()).isEqualTo("Caisse A");
    }
}