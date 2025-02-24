package com.zenika.codelab.archi.hexa.infrastructure.input.controler;

import com.zenika.codelab.archi.hexa.domain.model.ApiError;
import com.zenika.codelab.archi.hexa.domain.model.ChiffreAffaireGlobal;
import com.zenika.codelab.archi.hexa.domain.model.ChiffreAffaireParCaisse;
import com.zenika.codelab.archi.hexa.domain.port.input.CalculCAPort;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CalculateController {

    private final CalculCAPort calculCAPortService;

    @GetMapping(value = "/ca/{date}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Chiffre d'affaire calcule",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChiffreAffaireGlobal.class))})
    @ApiResponse(responseCode = "500", description = "Technical error has occurred.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    public ResponseEntity<ChiffreAffaireGlobal> calculTotalCA(@PathVariable(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(calculCAPortService.computeFromDate(date));
    }

    @GetMapping(value = "/ca/{date}/{caisse}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Chiffre d'affaire calcule",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChiffreAffaireParCaisse.class))})
    @ApiResponse(responseCode = "500", description = "Technical error has occurred.",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))})
    public ResponseEntity<ChiffreAffaireParCaisse> calculTotalByCaisse(@PathVariable(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                       @PathVariable(value = "caisse") String libelleCaisse) {
        return ResponseEntity.ok(calculCAPortService.computeFromDateForCaisse(date, libelleCaisse));
    }

}
