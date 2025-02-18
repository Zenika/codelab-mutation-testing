package com.zenika.codelab.archi.hexa.domain.model;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ChiffreAffaireGlobal(@NotNull List<ChiffreAffaireParCaisse> caisses, BigDecimal caTotal) {
}
