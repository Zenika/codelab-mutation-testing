package com.zenika.codelab.archi.hexa.domain.service;

import com.zenika.codelab.archi.hexa.domain.exception.DataQualityException;
import com.zenika.codelab.archi.hexa.domain.model.ArticleVO;
import com.zenika.codelab.archi.hexa.domain.model.CaisseVO;
import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;
import com.zenika.codelab.archi.hexa.domain.port.output.TicketServiceRepositoryPort;
import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;
import lombok.SneakyThrows;
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
import static org.mockito.ArgumentMatchers.*;
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
        assertThat(expected.ticketStatus().getCode()).isEqualTo("F");
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
        assertThat(expected.ticketStatus().getCode()).isEqualTo("A");
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
        }).isInstanceOf(DataQualityException.class);
    }

    @Test
    @DisplayName("Doit retourner ok car montant == à 0")
    @SneakyThrows
    void test_qualite_controle_facture_0() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(0.00))),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(0.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }


    @Test
    @DisplayName("Doit retourner ok car montant ==  null")
    @SneakyThrows
    void test_qualite_controle_facture_montant_null() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", null)),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, null);

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal()).isNull();
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }


    @Test
    @DisplayName("Controle si le montant est egale à 0")
    @SneakyThrows
    void test_qualite_controle_avoir_positif() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.AVOIR, BigDecimal.valueOf(15.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);

        assertThatThrownBy(() -> {
            ticketService.getTicketFromId(1L);
        }).isInstanceOf(DataQualityException.class);
    }


    @Test
    @DisplayName("Test get All ticket from caisse and date")
    @SneakyThrows
    void test_get_all_ticket_from_start_date_and_caisse() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", null)),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, null);

        when(repository.findAllTicketFromStartDateAndCaisse(any(), anyString())).thenReturn(List.of(ticket));
        var expected  = ticketService.getAllTicketFromStartDateAndCaisse(LocalDate.now(), "Caisse A");
        assertThat(expected).isNotNull();
        var ticketExpected = expected.get(0);
        assertThat(ticketExpected).isNotNull();
        assertThat(ticketExpected.id()).isEqualTo(1L);

    }


    @Test
    @DisplayName("Test get All ticket from date")
    @SneakyThrows
    void test_get_all_ticket_from_start_date() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", null)),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, null);

        when(repository.findTicketAfterDate(any())).thenReturn(List.of(ticket));
        var expected  = ticketService.getAllTicketFromStartDate(LocalDate.now());
        assertThat(expected).isNotNull();
        var ticketExpected = expected.get(0);
        assertThat(ticketExpected).isNotNull();
        assertThat(ticketExpected.id()).isEqualTo(1L);

    }



    @Test
    @DisplayName("Doit retourner ok car montant == à 0")
    @SneakyThrows
    void test_qualite_controle_garantie_0() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "perceuse", BigDecimal.valueOf(0.00))),
                new CaisseVO("Caisse A"), TicketStatus.GARANTIE, BigDecimal.valueOf(0.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);

        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Doit retourner ok car montant == à 0")
    @SneakyThrows
    void test_qualite_controle_garantie_plus() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "perceuse", BigDecimal.valueOf(0.00))),
                new CaisseVO("Caisse A"), TicketStatus.GARANTIE, BigDecimal.valueOf(10.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal().doubleValue()).isEqualTo(BigDecimal.valueOf(10.00).doubleValue());
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }


    @Test
    @DisplayName("Doit retourner ok car montant == à 0")
    @SneakyThrows
    void test_qualite_controle_garantie_minus() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "perceuse", BigDecimal.valueOf(0.00))),
                new CaisseVO("Caisse A"), TicketStatus.GARANTIE, BigDecimal.valueOf(-10.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal().doubleValue()).isEqualTo(BigDecimal.valueOf(-10.00).doubleValue());
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }
}