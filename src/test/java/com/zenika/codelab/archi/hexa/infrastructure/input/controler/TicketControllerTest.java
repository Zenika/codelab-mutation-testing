package com.zenika.codelab.archi.hexa.infrastructure.input.controler;

import com.zenika.codelab.archi.hexa.Main;
import com.zenika.codelab.archi.hexa.domain.exception.DataQualityException;
import com.zenika.codelab.archi.hexa.domain.model.ArticleVO;
import com.zenika.codelab.archi.hexa.domain.model.CaisseVO;
import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;
import com.zenika.codelab.archi.hexa.domain.port.input.TicketServicePort;
import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TicketServicePort ticketServicePort;

    @BeforeEach
    void setup() {
    }

    @Test
    @SneakyThrows
    void data_not_found() {
        var url = "/api/ticket/10";
        when(ticketServicePort.getTicketFromId(anyLong())).thenThrow(new DataNotFound("10"));
        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.startsWith("Pas de donnée en base pour le model 10")));
    }

    @Test
    @SneakyThrows
    void bad_data_quality_not_expected() {
        var url = "/api/ticket/10";
        when(ticketServicePort.getTicketFromId(anyLong())).thenThrow(new DataQualityException(new TicketVO(1L, LocalDate.now(), List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(-12.00))));
        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message", Matchers.startsWith("Echec controle du model Facture id 1, montant -12,00 ")));
    }


    @Test
    @SneakyThrows
    void data_found() {
        var url = "/api/ticket/1";
        when(ticketServicePort.getTicketFromId(anyLong())).thenReturn(new TicketVO(1L, LocalDate.now(), List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(12.00)));

        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.caisse.libelle", Matchers.is("Caisse A")))
                .andExpect(jsonPath("$.montantTotal", Matchers.is(12d)))
        ;
    }

}