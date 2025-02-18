package com.zenika.codelab.archi.hexa.infrastructure.repository;

import com.zenika.codelab.archi.hexa.infrastructure.entity.CaisseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CaisseRepository extends CrudRepository<CaisseEntity, Long> {

    @Query(value = "SELECT c FROM CaisseEntity c" +
            " where c.libelle=?1")
    CaisseEntity findByLibelle(String libelle);

}
