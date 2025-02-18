package com.zenika.codelab.archi.hexa.domain.port.output;

import com.zenika.codelab.archi.hexa.domain.model.CaisseVO;

import java.util.List;

public interface CaisseServiceRepositoryPort {

    List<CaisseVO> getAllCaisses();

    CaisseVO getFromId();
}
