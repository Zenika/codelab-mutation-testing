package com.zenika.codelab.archi.hexa.infrastructure.input.controler;

import com.zenika.codelab.archi.hexa.Main;
import com.zenika.codelab.archi.hexa.domain.model.CaisseVO;
import com.zenika.codelab.archi.hexa.domain.model.ChiffreAffaireGlobal;
import com.zenika.codelab.archi.hexa.domain.model.ChiffreAffaireParCaisse;
import com.zenika.codelab.archi.hexa.domain.port.input.CalculCAPort;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc(addFilters = false)
class CalculateControllerTest {


    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CalculCAPort calculCAPort;

    @BeforeEach
    void setup() {
    }


    @Test
    @SneakyThrows
    void invalid_param_computeFromDate() {
        var url = "/api/ca/truc";
        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.startsWith("Method parameter 'date': Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDate'")));
    }


    @Test
    @SneakyThrows
    void test_format_date() {
        var url = "/api/ca/2025-01-01";
        var chiffreAffaire = new ChiffreAffaireGlobal(List.of(new ChiffreAffaireParCaisse(new CaisseVO("A"), BigDecimal.ONE),
                new ChiffreAffaireParCaisse(new CaisseVO("B"), BigDecimal.ZERO)),
                BigDecimal.valueOf(12));

        when(calculCAPort.computeFromDate(any())).thenReturn(chiffreAffaire);

        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.caisses[0].ca", Matchers.is(1)))
                .andExpect(jsonPath("$.caisses[0].caisseVO.libelle", Matchers.equalTo("A")))
                .andExpect(jsonPath("$.caisses[1].ca", Matchers.is(0)))
                .andExpect(jsonPath("$.caisses[1].caisseVO.libelle", Matchers.equalTo("B")))
                .andExpect(jsonPath("$.caTotal", Matchers.is(12)));
    }


    @Test
    @SneakyThrows
    void good_param_computeFromDateForCaisse() {
        var url = "/api/ca/2020-01-01/1";
        when(calculCAPort.computeFromDateForCaisse(any(), anyString())).thenReturn(new ChiffreAffaireParCaisse(new CaisseVO("A"), BigDecimal.ZERO));

        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.caisseVO.libelle", Matchers.equalTo("A")))
                .andExpect(jsonPath("$.ca", Matchers.is(0)))
        ;
    }

    @Test
    @SneakyThrows
    void invalid_param_computeFromDateForCaisse() {
        var url = "/api/ca/truc/1";
        when(calculCAPort.computeFromDateForCaisse(any(), anyString())).thenReturn(new ChiffreAffaireParCaisse(new CaisseVO("A"), BigDecimal.ZERO));

        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.startsWith("Method parameter 'date': Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDate'")));
    }

}