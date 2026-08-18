package com.epfafrica.etatcivil.interfaces.rest;

import com.epfafrica.etatcivil.application.service.DemandeActeService;
import com.epfafrica.etatcivil.interfaces.dto.DemandeActeDTO;
import com.epfafrica.etatcivil.interfaces.dto.TraitementRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/demandes-actes")
public class DemandeActeController {

    private final DemandeActeService demandeActeService;

    public DemandeActeController(DemandeActeService demandeActeService) {
        this.demandeActeService = demandeActeService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> soumettre(Authentication auth,
                                        @RequestParam Long typeActeId,
                                        @RequestParam String infosDemandeur,
                                        @RequestParam(required = false) List<MultipartFile> pieces) {
        DemandeActeDTO dto = demandeActeService.soumettre(auth.getName(), typeActeId, infosDemandeur, pieces);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<DemandeActeDTO>> lister(Authentication auth) {
        boolean estOfficier = estOfficier(auth);
        List<DemandeActeDTO> demandes = estOfficier
                ? demandeActeService.listerToutes()
                : demandeActeService.listerPourCitoyen(auth.getName());
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenir(Authentication auth, @PathVariable Long id) {
        try {
            DemandeActeDTO dto = demandeActeService.obtenir(id, auth.getName(), estOfficier(auth));
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
    @PatchMapping("/{id}/traitement")
public ResponseEntity<?> traiter(Authentication auth, @PathVariable Long id,
                                  @RequestBody TraitementRequest request) {
    if (!estOfficier(auth)) {
        return ResponseEntity.status(403).body("Seul un officier peut traiter une demande");
    }
    try {
        DemandeActeDTO dto = demandeActeService.traiter(id, request);
        return ResponseEntity.ok(dto);
    } catch (NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    private boolean estOfficier(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OFFICIER"));
    }
}