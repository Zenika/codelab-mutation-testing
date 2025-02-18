package com.zenika.codelab.archi.hexa.infrastructure.repository;

import com.zenika.codelab.archi.hexa.infrastructure.entity.CaisseEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.zenika.codelab.archi.hexa.infrastructure.repository.EntityBuilder.buildCaisseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CaisseRepositoryTest extends AbstractRepositoryTest {

    private static final String CAISSE_A = "Caisse A";
    private static final String CAISSE_B = "Caisse B";

    @Autowired
    private CaisseRepository caisseRepository;


    @BeforeEach
    public void setUp() {
        log.info("Before test setup");
        // Initialize test data before each test method
        CaisseEntity c = buildCaisseEntity(CAISSE_A);
        caisseRepository.save(c);
        log.info("Before test setup");
    }

    @Test
    @DisplayName("save de la caisse B")
    void save_caisse() {
        log.info("save Caisse A: {}", CAISSE_B);
        CaisseEntity expected = CaisseEntity.builder().libelle(CAISSE_B).build();
        CaisseEntity caisseEntity = caisseRepository.save(expected);
        assertThat(caisseEntity).isNotNull();
        assertThat(caisseEntity.getLibelle()).isEqualTo(expected.getLibelle());
        assertThat(caisseEntity.getTicketCaisse()).isNull();

    }
}