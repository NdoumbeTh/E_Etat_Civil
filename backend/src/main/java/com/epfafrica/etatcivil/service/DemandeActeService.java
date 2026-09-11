package com.epfafrica.etatcivil.service;

import com.epfafrica.etatcivil.dto.DemandeActeDTO;
import com.epfafrica.etatcivil.dto.TraitementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DemandeActeService {

    DemandeActeDTO soumettre(
            String emailCitoyen,
            Long typeActeId,
            String infosDemandeur,
            List<MultipartFile> pieces
    );

    Page<DemandeActeDTO> listerPourCitoyen(
            String emailCitoyen,
            Pageable pageable
    );

    Page<DemandeActeDTO> listerToutes(
            Pageable pageable
    );

    DemandeActeDTO obtenir(
            Long id,
            String emailDemandeur,
            boolean estOfficier
    );

    DemandeActeDTO traiter(
            Long id,
            TraitementRequest request
    );
}