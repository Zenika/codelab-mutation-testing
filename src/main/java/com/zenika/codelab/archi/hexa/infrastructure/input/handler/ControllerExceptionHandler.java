package com.zenika.codelab.archi.hexa.infrastructure.input.handler;

import com.zenika.codelab.archi.hexa.domain.exception.DataQualityException;
import com.zenika.codelab.archi.hexa.domain.model.ApiError;
import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;
import com.zenika.codelab.archi.hexa.infrastructure.input.controler.CalculateController;
import com.zenika.codelab.archi.hexa.infrastructure.input.controler.TicketController;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice(basePackageClasses = {TicketController.class, CalculateController.class})
@NoArgsConstructor
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders h, HttpStatusCode s, WebRequest r) {
        return buildApiError(ex);
    }

    private ResponseEntity<Object> buildApiError(final Exception ex) {
        log.warn(ex.getMessage());
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFound.class)
    public final ResponseEntity<ApiError> dataNotFound(final DataNotFound ex) {
        return buildApiError(ex);
    }

    @ExceptionHandler(DataQualityException.class)
    public final ResponseEntity<ApiError> dataQualityException(final DataQualityException ex) {
        return buildApiError(ex);
    }

    private ResponseEntity<ApiError> buildApiError(final DataNotFound ex) {
        log.warn(ex.getMessage());
        final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ApiError> buildApiError(final DataQualityException ex) {
        log.warn(ex.getMessage());
        final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
