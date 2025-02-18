package com.zenika.codelab.archi.hexa.infrastructure.repository;

import com.zenika.codelab.archi.hexa.infrastructure.entity.ArticleEntity;
import com.zenika.codelab.archi.hexa.infrastructure.entity.CaisseEntity;
import com.zenika.codelab.archi.hexa.infrastructure.entity.TicketEntity;
import com.zenika.codelab.archi.hexa.infrastructure.entity.TicketStatusEntity;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@UtilityClass
public class EntityBuilder {
    public static TicketStatusEntity buildTicketStatusEntity(final String code, final String libelle) {
        return TicketStatusEntity.builder()
                .code(code)
                .libelle(libelle)
                .build();

    }

    public static CaisseEntity buildCaisseEntity(final String nomCaisse) {
        return CaisseEntity.builder().libelle(nomCaisse).build();
    }

    public static TicketEntity buildTicketEntityGarantie(final long idTicket, final String codeStatus, final String libelleStatus,
                                                         final String codeCaisse, final BigDecimal value) {
        return TicketEntity.builder()
                .id(idTicket)
                .articleList(null)
                .dateEmission(LocalDate.now())
                .ticketStatus(buildTicketStatusEntity(codeStatus, libelleStatus))
                .ticketCaisse(buildCaisseEntity(codeCaisse))
                .montant(value)
                .build();

    }


    public static List<ArticleEntity> buildArticles() {
        return Stream.of(buildTicketArticle(BigDecimal.valueOf(1), BigDecimal.valueOf(2), "pizza jambon", BigDecimal.valueOf(2)),
                buildTicketArticle(BigDecimal.valueOf(2), BigDecimal.valueOf(5), "pizza ananas + jambon", BigDecimal.valueOf(10))).toList();
    }

    public static ArticleEntity buildTicketArticle(final BigDecimal quantite, final BigDecimal prixUnitaire, final String description,
                                                   final BigDecimal value) {
        return ArticleEntity.builder()
                .quantite(quantite)
                .prixUnite(prixUnitaire)
                .libelle(description)
                .montant(value).build();
    }

    /**
     * @param idTicket
     * @param codeStatus
     * @param libelle
     * @param codeCaisse
     * @return un ticket d'un montant de 12.00
     */
    public static TicketEntity buildTicketEntityFacture(final long idTicket, final String codeStatus, final String libelle,
                                                        final String codeCaisse) {
        var articles = buildArticles();
        var montant = articles.stream().map(ArticleEntity::getMontant).mapToDouble(BigDecimal::doubleValue).sum();
        return TicketEntity.builder()
                .id(idTicket)
                .articleList(articles)
                .dateEmission(LocalDate.now())
                .ticketStatus(buildTicketStatusEntity(codeStatus, libelle))
                .ticketCaisse(buildCaisseEntity(codeCaisse))
                .montant(BigDecimal.valueOf(montant))
                .build();

    }
}
