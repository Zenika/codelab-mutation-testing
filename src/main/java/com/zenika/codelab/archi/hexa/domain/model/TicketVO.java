package com.zenika.codelab.archi.hexa.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @param id
 * @param dateEmission
 * @param articles
 * @param caisse
 * @param ticketStatus
 * @param montantTotal
 */
public record TicketVO(Long id, LocalDate dateEmission, List<ArticleVO> articles, CaisseVO caisse,
                       TicketStatus ticketStatus,
                       BigDecimal montantTotal) {

}
