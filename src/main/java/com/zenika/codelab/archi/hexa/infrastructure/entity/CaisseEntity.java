package com.zenika.codelab.archi.hexa.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "caisse")
public class CaisseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "caisse_generator")
    private long id;

    private String libelle;

    @OneToOne(mappedBy = "ticketCaisse")
    private TicketEntity ticketCaisse;

}
