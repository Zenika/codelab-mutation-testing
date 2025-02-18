package com.zenika.codelab.archi.hexa.domain.model;

import java.math.BigDecimal;

public record ArticleVO(Long id, BigDecimal montant, BigDecimal quantite, String libelle, BigDecimal prixUnite) {
}
