package com.epfafrica.etatcivil.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DemandeActeDTO(
        Long id,
        String typeActeLibelle,
        String infosDemandeur,
        String statut,
        String motifRejet,
        LocalDateTime dateDepot,
        List<PieceJustificativeDTO> pieces,
        String numeroActe,
        String urlActe
) {
}