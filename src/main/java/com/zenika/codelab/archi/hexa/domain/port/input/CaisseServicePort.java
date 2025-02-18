package com.zenika.codelab.archi.hexa.domain.port.input;

import com.zenika.codelab.archi.hexa.domain.model.CaisseVO;

import java.util.List;

public interface CaisseServicePort {

    List<CaisseVO> getAllCaisses();

    CaisseVO getFromId();
}
