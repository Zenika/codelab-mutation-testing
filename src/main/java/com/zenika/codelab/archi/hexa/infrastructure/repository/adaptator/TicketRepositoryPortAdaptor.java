package com.zenika.codelab.archi.hexa.infrastructure.repository.adaptator;

import com.zenika.codelab.archi.hexa.domain.model.ArticleVO;
import com.zenika.codelab.archi.hexa.domain.model.CaisseVO;
import com.zenika.codelab.archi.hexa.domain.model.TicketVO;
import com.zenika.codelab.archi.hexa.domain.port.output.TicketServiceRepositoryPort;
import com.zenika.codelab.archi.hexa.infrastructure.entity.CaisseEntity;
import com.zenika.codelab.archi.hexa.infrastructure.entity.TicketEntity;
import com.zenika.codelab.archi.hexa.infrastructure.exception.DataNotFound;
import com.zenika.codelab.archi.hexa.infrastructure.repository.CaisseRepository;
import com.zenika.codelab.archi.hexa.infrastructure.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TicketRepositoryPortAdaptor implements TicketServiceRepositoryPort {

    private final TicketRepository ticketRepository;
    private final CaisseRepository caisseRepository;
    private final TicketStatusPortAdaptor statusPortAdaptor;

    @Override
    public TicketVO getFromId(Long id) {

        return Optional.ofNullable(ticketRepository.findById(id)).orElseThrow(() -> new DataNotFound(String.valueOf(id)))
                .map(this::buildTicketVO).orElseThrow(() -> new DataNotFound(String.valueOf(id)));
    }

    @Override
    public List<TicketVO> findTicketAfterDate(LocalDate date) {
        var ticketList = ticketRepository.findTicketAfterDate(date);
        return ticketList.stream().map(this::buildTicketVO).toList();
    }

    @Override
    public List<TicketVO> findAllTicketFromStartDateAndCaisse(LocalDate date, String libelleCaisse) {
        CaisseEntity caisseEntity = caisseRepository.findByLibelle(libelleCaisse);

        var ticketList = ticketRepository.findAllTicketFromStartDateAndCaisse(date, caisseEntity);
        return ticketList.stream().map(this::buildTicketVO).toList();
    }


    private TicketVO buildTicketVO(TicketEntity t) {
        var articles = t.getArticleList().stream().map(articleEntity ->
                new ArticleVO(articleEntity.getId(), articleEntity.getMontant(), articleEntity.getQuantite(),
                        articleEntity.getLibelle(), articleEntity.getPrixUnite())).toList();

        // convertion status
        var status = statusPortAdaptor.convertFromEntity(t.getTicketStatus());

        var montant = articles.stream().map(ArticleVO::montant).mapToDouble(BigDecimal::doubleValue).sum();

        return new TicketVO(t.getId(), t.getDateEmission(), articles,
                new CaisseVO(t.getTicketCaisse().getLibelle())
                , status, BigDecimal.valueOf(montant));
    }
}
