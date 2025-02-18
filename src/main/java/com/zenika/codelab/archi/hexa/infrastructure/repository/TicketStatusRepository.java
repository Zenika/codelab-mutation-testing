package com.zenika.codelab.archi.hexa.infrastructure.repository;

import com.zenika.codelab.archi.hexa.infrastructure.entity.TicketStatusEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketStatusRepository extends CrudRepository<TicketStatusEntity, Long> {

    @Query(value = "SELECT ts FROM TicketStatusEntity ts" +
            " where ts.code = ?1")
    TicketStatusEntity findByCode(String code);
}
