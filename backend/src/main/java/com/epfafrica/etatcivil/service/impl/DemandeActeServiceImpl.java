package com.epfafrica.etatcivil.service.impl;

import com.epfafrica.etatcivil.dto.DemandeActeDTO;
import com.epfafrica.etatcivil.dto.TraitementRequest;
import com.epfafrica.etatcivil.mapper.DemandeActeMapper;
import com.epfafrica.etatcivil.model.ActeDelivre;
import com.epfafrica.etatcivil.model.DemandeActe;
import com.epfafrica.etatcivil.model.PieceJustificative;
import com.epfafrica.etatcivil.model.TypeActe;
import com.epfafrica.etatcivil.model.Utilisateur;
import com.epfafrica.etatcivil.notification.NotificationService;
import com.epfafrica.etatcivil.repository.DemandeActeRepository;
import com.epfafrica.etatcivil.repository.TypeActeRepository;
import com.epfafrica.etatcivil.repository.UtilisateurRepository;
import com.epfafrica.etatcivil.service.DemandeActeService;
import com.epfafrica.etatcivil.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epfafrica.etatcivil.model.ActeDelivre;
import com.epfafrica.etatcivil.service.ActePdfService;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DemandeActeServiceImpl implements DemandeActeService {

    private final DemandeActeRepository demandeActeRepository;
    private final TypeActeRepository typeActeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final DemandeActeMapper demandeActeMapper;
    private final ActePdfService actePdfService;

    public DemandeActeServiceImpl(DemandeActeRepository demandeActeRepository,
                                   TypeActeRepository typeActeRepository,
                                   UtilisateurRepository utilisateurRepository,
                                   FileStorageService fileStorageService,
                                   NotificationService notificationService,
                                   DemandeActeMapper demandeActeMapper, ActePdfService actePdfService) {
        this.demandeActeRepository = demandeActeRepository;
        this.typeActeRepository = typeActeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
        this.demandeActeMapper = demandeActeMapper;
        this.actePdfService = actePdfService;
    }

    @Override
public DemandeActeDTO traiter(Long id, TraitementRequest request) {
    DemandeActe demande = demandeActeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Demande introuvable"));

    if (request.decision() == TraitementRequest.Decision.VALIDEE) {
        demande.valider();

        if (demande.getActeDelivre() == null) { // RG-06 : jamais régénérer
            String prefixe = demande.getTypeActe().getLibelle();
            prefixe = prefixe.length() >= 3 ? prefixe.substring(0, 3) : prefixe;
            String numeroUnique = prefixe + "-" + Year.now() + "-" + String.format("%06d", demande.getId());

            String cheminPdf = actePdfService.genererPdf(demande, numeroUnique);

            ActeDelivre acte = new ActeDelivre();
            acte.setDemandeActe(demande);
            acte.setNumeroUnique(numeroUnique);
            acte.setCheminFichierPdf(cheminPdf);
            demande.setActeDelivre(acte);
        }
    } else {
        demande.rejeter(request.motif());
    }

    DemandeActe saved = demandeActeRepository.save(demande);
    notificationService.notifierChangementStatut(saved);
    return demandeActeMapper.toDTO(saved);
}

    @Override
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
        return demandeActeMapper.toDTO(saved);
    }

    @Override
    public List<DemandeActeDTO> listerPourCitoyen(String emailCitoyen) {
        Utilisateur citoyen = utilisateurRepository.findByEmail(emailCitoyen)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable"));
        return demandeActeRepository.findByCitoyenId(citoyen.getId()).stream()
                .map(demandeActeMapper::toDTO)
                .toList();
    }

    @Override
    public List<DemandeActeDTO> listerToutes() {
        return demandeActeRepository.findAll().stream()
                .map(demandeActeMapper::toDTO)
                .toList();
    }

    @Override
    public DemandeActeDTO obtenir(Long id, String emailDemandeur, boolean estOfficier) {
        DemandeActe demande = demandeActeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Demande introuvable"));

        if (!estOfficier && !demande.getCitoyen().getEmail().equals(emailDemandeur)) {
            throw new SecurityException("Accès refusé à cette demande");
        }
        return demandeActeMapper.toDTO(demande);
    }
}
