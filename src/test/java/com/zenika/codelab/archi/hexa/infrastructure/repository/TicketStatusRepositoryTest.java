package com.zenika.codelab.archi.hexa.infrastructure.repository;

import com.zenika.codelab.archi.hexa.infrastructure.entity.TicketStatusEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import static com.zenika.codelab.archi.hexa.infrastructure.repository.EntityBuilder.buildTicketStatusEntity;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
class TicketStatusRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private TicketStatusRepository ticketStatusRepository;

    @BeforeEach
    public void setUp() {
        log.info("Before test setup");
        // Initialize test data before each test method
        ticketStatusRepository.save(buildTicketStatusEntity("c", "libelle c"));
        ticketStatusRepository.save(buildTicketStatusEntity("d", "libelle d"));

        log.info("Before test setup");
    }


    @DisplayName("recherche ")
    @ParameterizedTest
    @ValueSource(strings = {"c", "d"})
    void get_status_from_code(final String code) {
        log.info("get status [{}]", code);
        var te = ticketStatusRepository.findByCode(code);
        assertThat(te).isNotNull();
        assertThat(te.getCode()).isEqualTo(code);
        assertThat(te.getLibelle()).isNotNull();
        assertThat(te.getLibelle()).contains(String.format("libelle %s", code));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b"})
    @DisplayName("save status ticket entity ")
    void save_caisse(final String code) {
        log.info("save new status : {}", code);
        var size = ticketStatusRepository.count();
        var te = buildTicketStatusEntity(code, String.format("libelle %s", code));
        ticketStatusRepository.save(te);

        var listStatus = ticketStatusRepository.findAll();
        assertThat(listStatus).isNotNull().hasSize((int) size + 1);
        listStatus.forEach(this::testEntity);

    }

    private void testEntity(TicketStatusEntity e) {
        assertThat(e).isNotNull();
        assertThat(e.getId()).isBetween(1L, 4L);
        assertThat(e.getCode()).isIn("a", "b", "c", "d");
        assertThat(e.getLibelle()).isIn("libelle a", "libelle b", "libelle c", "libelle d");
    }

}