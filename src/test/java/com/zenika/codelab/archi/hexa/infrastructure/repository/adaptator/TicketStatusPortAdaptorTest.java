package com.zenika.codelab.archi.hexa.infrastructure.repository.adaptator;

import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;
import com.zenika.codelab.archi.hexa.infrastructure.repository.TicketStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.zenika.codelab.archi.hexa.infrastructure.repository.EntityBuilder.buildTicketStatusEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketStatusPortAdaptorTest {

    @InjectMocks
    private TicketStatusPortAdaptor ticketStatusPortAdaptor;

    @Mock
    private TicketStatusRepository repository;

    @ParameterizedTest
    @ValueSource(strings = {"F", "A",})
    @DisplayName("Convertion entité en domain")
    void test_convertion_status_connu(String code) {
        var entity = buildTicketStatusEntity(code, String.format("test %s", code));

        when(repository.findByCode(anyString())).thenReturn(entity);
        var expected = ticketStatusPortAdaptor.getStatus(code);

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(entity.getCode());
    }

    @Test
    @DisplayName("Convertion impossible")
    void erreur_mapping_entity_model() {
        var code = "Truc";
        var entity = buildTicketStatusEntity(code, String.format("test %s", code));
        when(repository.findByCode(anyString())).thenReturn(entity);

        assertThatThrownBy(() -> {
            ticketStatusPortAdaptor.getStatus(code);
        }).isInstanceOf(DataNotFound.class)
                .hasMessageContaining("Pas de donnée en base pour le model ");

    }

}