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
    }

}