package com.epfafrica.etatcivil.mapper;

import com.epfafrica.etatcivil.dto.PieceJustificativeDTO;
import com.epfafrica.etatcivil.model.PieceJustificative;
import org.springframework.stereotype.Component;

@Component
public class PieceJustificativeMapper {

    public PieceJustificativeDTO toDTO(PieceJustificative piece) {
        return new PieceJustificativeDTO(piece.getId(), piece.getNomFichier(), piece.getUrl());
    }
}
