package com.zenika.codelab.archi.hexa.infrastructure.input.handler;

import com.zenika.codelab.archi.hexa.domain.exception.DataQualityException;
import com.zenika.codelab.archi.hexa.domain.model.ApiError;
import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;
import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerExceptionHandlerTest {

    private final ControllerExceptionHandler handler = new ControllerExceptionHandler();

    @Test
    public void test_handler_exception_error_40x() {
        ResponseEntity<ApiError> ret = handler.dataNotFound(new DataNotFound("CODE"));
        assertThat(ret.getStatusCode().value()).isEqualTo(404);
        var apiError =  ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(404);
        assertThat(apiError.message()).contains("Pas de donnée en base pour le model ");
    }

    @Test
    public void test_handler_exception_error_50x() {
        ResponseEntity<ApiError> ret = handler.dataQualityException(new DataQualityException(new TicketVO(1L, LocalDate.now(), null, null, TicketStatus.UNKNOW, BigDecimal.ONE)));
        assertThat(ret.getStatusCode().value()).isEqualTo(500);
        var apiError =  ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(500);
        assertThat(apiError.message()).contains("Echec controle du model ");
    }

    @Test
    public void test_handler_exception_error_400() {
        ResponseEntity<Object> ret = handler.handleTypeMismatch(new TypeMismatchException("truc", String.class), null, null, null);
        assertThat(ret.getStatusCode().value()).isEqualTo(400);
        var apiError =  (ApiError)ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(400);
        assertThat(apiError.message()).contains("Failed to convert value of type ");
    }

}