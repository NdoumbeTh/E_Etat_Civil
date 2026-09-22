package com.epfafrica.etatcivil.service.impl;

import com.epfafrica.etatcivil.dto.StatsDTO;
import com.epfafrica.etatcivil.enums.StatutDemande;
import com.epfafrica.etatcivil.model.DemandeActe;
import com.epfafrica.etatcivil.repository.DemandeActeRepository;
import com.epfafrica.etatcivil.service.StatsService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final DemandeActeRepository demandeActeRepository;

    public StatsServiceImpl(DemandeActeRepository demandeActeRepository) {
        this.demandeActeRepository = demandeActeRepository;
    }

    @Override
    public StatsDTO calculer() {
        List<DemandeActe> toutes = demandeActeRepository.findAll();

        long total = toutes.size();
        long deposees = compter(toutes, StatutDemande.DEPOSEE);
        long enTraitement = compter(toutes, StatutDemande.EN_TRAITEMENT);
        long validees = compter(toutes, StatutDemande.VALIDEE);
        long rejetees = compter(toutes, StatutDemande.REJETEE);

        Map<String, Long> parType = toutes.stream()
                .collect(Collectors.groupingBy(d -> d.getTypeActe().getLibelle(), Collectors.counting()));

        List<DemandeActe> traitees = toutes.stream()
                .filter(d -> d.getDateTraitement() != null)
                .toList();

        Double delaiMoyenHeures = null;
        if (!traitees.isEmpty()) {
            double moyenneMinutes = traitees.stream()
                    .mapToLong(d -> Duration.between(d.getDateDepot(), d.getDateTraitement()).toMinutes())
                    .average()
                    .orElse(0);
            delaiMoyenHeures = moyenneMinutes / 60.0;
        }

        return new StatsDTO(total, deposees, enTraitement, validees, rejetees, parType, delaiMoyenHeures);
    }

    private long compter(List<DemandeActe> demandes, StatutDemande statut) {
        return demandes.stream().filter(d -> d.getStatut() == statut).count();
    }
}