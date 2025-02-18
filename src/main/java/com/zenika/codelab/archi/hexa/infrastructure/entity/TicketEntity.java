package com.zenika.codelab.archi.hexa.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket")
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_generator")
    private Long id;

    private LocalDate dateEmission;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleEntity> articleList;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id", name = "id_status")
    private TicketStatusEntity ticketStatus;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id", name = "id_caisse")
    private CaisseEntity ticketCaisse;

    private BigDecimal montant;
}