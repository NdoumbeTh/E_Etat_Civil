package com.epfafrica.etatcivil.application.service;

import com.epfafrica.etatcivil.domain.model.DemandeActe;
import com.epfafrica.etatcivil.domain.model.PieceJustificative;
import com.epfafrica.etatcivil.domain.model.TypeActe;
import com.epfafrica.etatcivil.domain.model.Utilisateur;
import com.epfafrica.etatcivil.infrastructure.persistence.DemandeActeRepository;
import com.epfafrica.etatcivil.infrastructure.persistence.TypeActeRepository;
import com.epfafrica.etatcivil.infrastructure.persistence.UtilisateurRepository;
import com.epfafrica.etatcivil.infrastructure.storage.FileStorageService;
import com.epfafrica.etatcivil.interfaces.dto.DemandeActeDTO;
import com.epfafrica.etatcivil.interfaces.dto.PieceJustificativeDTO;
import com.epfafrica.etatcivil.interfaces.dto.TraitementRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.epfafrica.etatcivil.application.service.NotificationService;
import com.epfafrica.etatcivil.interfaces.dto.TraitementRequest;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DemandeActeService {

    private final DemandeActeRepository demandeActeRepository;
    private final TypeActeRepository typeActeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;  

    public DemandeActeService(DemandeActeRepository demandeActeRepository,
                               TypeActeRepository typeActeRepository,
                               UtilisateurRepository utilisateurRepository,
                               FileStorageService fileStorageService,
                               NotificationService notificationService) {
        this.demandeActeRepository = demandeActeRepository;
        this.typeActeRepository = typeActeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
    }

    public DemandeActeDTO traiter(Long id, TraitementRequest request) {
    DemandeActe demande = demandeActeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Demande introuvable"));

    if (request.decision() == TraitementRequest.Decision.VALIDEE) {
        demande.valider();
    } else {
        demande.rejeter(request.motif()); // lève IllegalArgumentException si motif manquant
    }

    DemandeActe saved = demandeActeRepository.save(demande);
    notificationService.notifierChangementStatut(saved);
    return toDTO(saved);
}
    public DemandeActeDTO soumettre(String emailCitoyen, Long typeActeId, String infosDemandeur, List<MultipartFile> pieces) {
        Utilisateur citoyen = utilisateurRepository.findByEmail(emailCitoyen)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable"));
        TypeActe typeActe = typeActeRepository.findById(typeActeId)
                .orElseThrow(() -> new NoSuchElementException("Type d'acte introuvable"));

        DemandeActe demande = new DemandeActe();
        demande.setCitoyen(citoyen);
        demande.setTypeActe(typeActe);
        demande.setInfosDemandeur(infosDemandeur);

        if (pieces != null) {
            for (MultipartFile file : pieces) {
                if (file.isEmpty()) continue;
                String url = fileStorageService.store(file);
                PieceJustificative piece = new PieceJustificative();
                piece.setDemandeActe(demande);
                piece.setNomFichier(file.getOriginalFilename());
                piece.setUrl(url);
                demande.getPieces().add(piece);
            }
        }

        DemandeActe saved = demandeActeRepository.save(demande);
        return toDTO(saved);
    }

    /** RG-04 : un CITOYEN ne consulte que ses propres demandes. */
    public List<DemandeActeDTO> listerPourCitoyen(String emailCitoyen) {
        Utilisateur citoyen = utilisateurRepository.findByEmail(emailCitoyen)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable"));
        return demandeActeRepository.findByCitoyenId(citoyen.getId()).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DemandeActeDTO> listerToutes() {
        return demandeActeRepository.findAll().stream().map(this::toDTO).toList();
    }

    public DemandeActeDTO obtenir(Long id, String emailDemandeur, boolean estOfficier) {
        DemandeActe demande = demandeActeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Demande introuvable"));

        if (!estOfficier && !demande.getCitoyen().getEmail().equals(emailDemandeur)) {
            throw new SecurityException("Accès refusé à cette demande");
        }
        return toDTO(demande);
    }

    private DemandeActeDTO toDTO(DemandeActe d) {
        List<PieceJustificativeDTO> pieces = d.getPieces().stream()
                .map(p -> new PieceJustificativeDTO(p.getId(), p.getNomFichier(), p.getUrl()))
                .toList();
        return new DemandeActeDTO(
                d.getId(),
                d.getTypeActe().getLibelle(),
                d.getInfosDemandeur(),
                d.getStatut().name(),
                d.getMotifRejet(),
                d.getDateDepot(),
                pieces
        );
    }
}