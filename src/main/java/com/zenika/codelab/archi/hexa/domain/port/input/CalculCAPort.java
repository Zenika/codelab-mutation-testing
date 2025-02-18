package com.zenika.codelab.archi.hexa.domain.port.input;

import com.zenika.codelab.archi.hexa.domain.model.ChiffreAffaireGlobal;
import com.zenika.codelab.archi.hexa.domain.model.ChiffreAffaireParCaisse;

import java.time.LocalDate;

public interface CalculCAPort {

    ChiffreAffaireGlobal computeFromDate(LocalDate date);

    ChiffreAffaireParCaisse computeFromDateForCaisse(LocalDate date, String libelleCaisse);
}
