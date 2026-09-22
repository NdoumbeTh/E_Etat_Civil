package com.epfafrica.etatcivil.dto;

import java.util.Map;

public record StatsDTO(
        long total,
        long deposees,
        long enTraitement,
        long validees,
        long rejetees,
        Map<String, Long> parType,
        Double delaiMoyenTraitementHeures // null si aucune demande traitée
) {
}