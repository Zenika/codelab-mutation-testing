package com.zenika.codelab.archi.hexa.infrastructure.input.controler;
import com.zenika.codelab.archi.hexa.domain.exception.DataQualityException;
import com.zenika.codelab.archi.hexa.domain.model.ApiError;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;
import com.zenika.codelab.archi.hexa.domain.port.input.TicketServicePort;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TicketController {

    private final TicketServicePort service;

    @GetMapping(value = "/ticket/{id}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Ticket trouvé",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TicketVO.class))})
    @ApiResponse(responseCode = "400", description = "Erreur lors de l'appel param incorrect.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode = "404", description = "Ticket non trouvé.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    @ApiResponse(responseCode = "500", description = "Technical error has occurred.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    public ResponseEntity<TicketVO> getTicket(@PathVariable Long id) throws DataQualityException {
        return ResponseEntity.ok(service.getTicketFromId(id));
    }

}
