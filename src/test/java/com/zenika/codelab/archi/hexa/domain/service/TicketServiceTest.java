package com.zenika.codelab.archi.hexa.domain.service;

import com.zenika.codelab.archi.hexa.domain.exception.DataQualityException;
import com.zenika.codelab.archi.hexa.domain.model.ArticleVO;
import com.zenika.codelab.archi.hexa.domain.model.CaisseVO;
import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;
import com.zenika.codelab.archi.hexa.domain.port.output.TicketServiceRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketServiceRepositoryPort repository;


    // ticket -> id, dateEmission, listArticle,caisse status, montant
    // article -> id, montant, quantite,  libelle,  prixUnite
    @Test
    @DisplayName("Doit retourner un ticket facture")
    void test_qualite_donnee_facture() throws DataQualityException {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(12.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected = ticketService.getTicketFromId(1L);

        assertThat(expected).isNotNull();
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }


    @Test
    @DisplayName("Doit retourner un ticket avoir valide")
    void test_qualite_donnee_avoir() throws DataQualityException {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.AVOIR, BigDecimal.valueOf(-12.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected = ticketService.getTicketFromId(1L);

        assertThat(expected).isNotNull();
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }


    @Test
    @DisplayName("Doit retourner une exception car c'est une facture et le montant est négative")
    void test_qualite_controle() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(-12.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        assertThatThrownBy(() -> {
            ticketService.getTicketFromId(1L);
        }).isInstanceOf(DataQualityException.class)
                .hasMessageContaining("Echec controle du model ");
    }
}