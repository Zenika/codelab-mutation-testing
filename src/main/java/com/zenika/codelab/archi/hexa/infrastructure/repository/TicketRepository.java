package com.zenika.codelab.archi.hexa.infrastructure.repository;

import com.zenika.codelab.archi.hexa.infrastructure.entity.CaisseEntity;
import com.zenika.codelab.archi.hexa.infrastructure.entity.TicketEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends CrudRepository<TicketEntity, Long> {

    @Query(value = "SELECT t FROM TicketEntity t" +
            " where t.dateEmission >= :date")
    List<TicketEntity> findTicketAfterDate(LocalDate date);


    @Query(value = "SELECT t FROM TicketEntity t" +
            " where t.dateEmission >= :date and t.ticketCaisse = :caisseEntity"
    )
    List<TicketEntity> findAllTicketFromStartDateAndCaisse(LocalDate date, CaisseEntity caisseEntity);
}
