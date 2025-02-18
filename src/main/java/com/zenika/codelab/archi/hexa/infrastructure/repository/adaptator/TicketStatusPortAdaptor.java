package com.zenika.codelab.archi.hexa.infrastructure.repository.adaptator;

import com.zenika.codelab.archi.hexa.domain.model.TicketStatus;
import com.zenika.codelab.archi.hexa.domain.port.output.TicketStatusRepositoryPort;
import com.zenika.codelab.archi.hexa.infrastructure.entity.TicketStatusEntity;
import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;
import com.zenika.codelab.archi.hexa.infrastructure.repository.TicketStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketStatusPortAdaptor implements TicketStatusRepositoryPort {
    private TicketStatusRepository repository;

    @Override
    public TicketStatus getStatus(String code) throws DataNotFound {
        var ts = repository.findByCode(code);
        return Optional.ofNullable(ts).map(this::convertFromEntity).orElseThrow(() -> new DataNotFound(code));
    }

    public TicketStatus convertFromEntity(TicketStatusEntity ts) {
        return switch (ts.getCode()) {
            case "F" -> TicketStatus.FACTURE;
            case "A" -> TicketStatus.AVOIR;
            case "R" -> TicketStatus.RETOUR;
            case "G" -> TicketStatus.GARANTIE;
            case "T" -> TicketStatus.TEST;
            case "U" -> TicketStatus.UNKNOW;
            default -> null;
        };

    }
}
