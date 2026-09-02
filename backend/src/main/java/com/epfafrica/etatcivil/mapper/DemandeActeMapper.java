package com.epfafrica.etatcivil.mapper;

import com.epfafrica.etatcivil.dto.DemandeActeDTO;
import com.epfafrica.etatcivil.dto.PieceJustificativeDTO;
import com.epfafrica.etatcivil.model.DemandeActe;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemandeActeMapper {

    private final PieceJustificativeMapper pieceJustificativeMapper;

    public DemandeActeMapper(PieceJustificativeMapper pieceJustificativeMapper) {
        this.pieceJustificativeMapper = pieceJustificativeMapper;
    }

    public DemandeActeDTO toDTO(DemandeActe demande) {
    List<PieceJustificativeDTO> pieces = demande.getPieces().stream()
            .map(pieceJustificativeMapper::toDTO)
            .toList();

    String numeroActe = demande.getActeDelivre() != null ? demande.getActeDelivre().getNumeroUnique() : null;
    String urlActe = demande.getActeDelivre() != null ? demande.getActeDelivre().getCheminFichierPdf() : null;

    return new DemandeActeDTO(
            demande.getId(),
            demande.getTypeActe().getLibelle(),
            demande.getInfosDemandeur(),
            demande.getStatut().name(),
            demande.getMotifRejet(),
            demande.getDateDepot(),
            pieces,
            numeroActe,
            urlActe
    );
}
}
