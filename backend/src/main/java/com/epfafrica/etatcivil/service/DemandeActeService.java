package com.epfafrica.etatcivil.service;

import com.epfafrica.etatcivil.dto.DemandeActeDTO;
import com.epfafrica.etatcivil.dto.TraitementRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DemandeActeService {

    DemandeActeDTO soumettre(String emailCitoyen, Long typeActeId, String infosDemandeur, List<MultipartFile> pieces);

    /** RG-04 : un CITOYEN ne consulte que ses propres demandes. */
    List<DemandeActeDTO> listerPourCitoyen(String emailCitoyen);

    List<DemandeActeDTO> listerToutes();

    DemandeActeDTO obtenir(Long id, String emailDemandeur, boolean estOfficier);

    DemandeActeDTO traiter(Long id, TraitementRequest request);
}
