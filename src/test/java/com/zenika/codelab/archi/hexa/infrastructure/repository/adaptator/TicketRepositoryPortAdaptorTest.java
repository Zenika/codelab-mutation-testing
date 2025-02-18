package com.zenika.codelab.archi.hexa.infrastructure.repository.adaptator;

import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.infrastructure.repository.CaisseRepository;
import com.zenika.codelab.archi.hexa.infrastructure.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.zenika.codelab.archi.hexa.infrastructure.repository.EntityBuilder.buildCaisseEntity;
import static com.zenika.codelab.archi.hexa.infrastructure.repository.EntityBuilder.buildTicketEntityFacture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketRepositoryPortAdaptorTest {

    @InjectMocks
    private TicketRepositoryPortAdaptor ticketRepositoryPortAdaptor;

    @Mock
    private TicketRepository repository;

    @Mock
    private TicketStatusPortAdaptor adaptorStatus;

    @Mock
    private CaisseRepository caisseRepository;

    @Test
    void test_convertion_ticket_vente() {
        var ticket = buildTicketEntityFacture(1L, "F", "libelle", "Caisse A");
        when(repository.findById(anyLong())).thenReturn(Optional.of(ticket));
        when(adaptorStatus.convertFromEntity(ticket.getTicketStatus())).thenReturn(TicketStatus.FACTURE);

        var ticketVO = ticketRepositoryPortAdaptor.getFromId(1L);

        // assert
        assertThat(ticketVO).isNotNull();
        assertThat(ticketVO.montantTotal()).isEqualTo(BigDecimal.valueOf(12.00));
        assertThat(ticketVO.ticketStatus().getLibelle()).isEqualToIgnoringCase(TicketStatus.FACTURE.getLibelle());
        assertThat(ticketVO.articles()).isNotNull().hasSize(2);
    }

    @Test
    void test_recherche_ticket_avec_date() {
        var ticket = buildTicketEntityFacture(1L, "F", "libelle", "Caisse A");
        when(repository.findTicketAfterDate(any())).thenReturn(List.of(ticket));
        when(adaptorStatus.convertFromEntity(ticket.getTicketStatus())).thenReturn(TicketStatus.FACTURE);

        var ticketVO = ticketRepositoryPortAdaptor.findTicketAfterDate(LocalDate.now());

        // assert
        assertThat(ticketVO).isNotNull().hasSize(1);
        var ticketExpected = ticketVO.get(0);

        assertThat(ticketExpected.montantTotal()).isEqualTo(BigDecimal.valueOf(12.00));
        assertThat(ticketExpected.ticketStatus().getLibelle()).isEqualToIgnoringCase(TicketStatus.FACTURE.getLibelle());
        assertThat(ticketExpected.articles()).isNotNull().hasSize(2);
    }


    @Test
    void test_recherche_ticket_avec_date_et_caisse() {
        var ticket = buildTicketEntityFacture(1L, "F", "libelle", "Caisse A");
        when(repository.findAllTicketFromStartDateAndCaisse(any(), any())).thenReturn(List.of(ticket));
        when(caisseRepository.findByLibelle(any())).thenReturn(buildCaisseEntity("Caisse A"));
        when(adaptorStatus.convertFromEntity(ticket.getTicketStatus())).thenReturn(TicketStatus.FACTURE);

        var ticketVO = ticketRepositoryPortAdaptor.findAllTicketFromStartDateAndCaisse(LocalDate.now(), "CAISSE A");

        // assert
        assertThat(ticketVO).isNotNull().hasSize(1);
        var ticketExpected = ticketVO.get(0);//.getFirst();

        assertThat(ticketExpected.montantTotal()).isEqualTo(BigDecimal.valueOf(12.00));
        assertThat(ticketExpected.ticketStatus().getLibelle()).isEqualToIgnoringCase(TicketStatus.FACTURE.getLibelle());
        assertThat(ticketExpected.articles()).isNotNull().hasSize(2);
    }

}