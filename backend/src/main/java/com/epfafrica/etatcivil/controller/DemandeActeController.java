package com.epfafrica.etatcivil.controller;

import com.epfafrica.etatcivil.dto.DemandeActeDTO;
import com.epfafrica.etatcivil.dto.TraitementRequest;
import com.epfafrica.etatcivil.service.DemandeActeService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    public ResponseEntity<?> soumettre(
            Authentication auth,
            @RequestParam Long typeActeId,
            @RequestParam String infosDemandeur,
            @RequestParam(required = false) List<MultipartFile> pieces
    ) {
        DemandeActeDTO dto = demandeActeService.soumettre(
                auth.getName(),
                typeActeId,
                infosDemandeur,
                pieces
        );

        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<DemandeActeDTO>> lister(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "dateDepot")
        );

        boolean estOfficier = estOfficier(auth);

        Page<DemandeActeDTO> demandes;

        if (estOfficier) {
            demandes = demandeActeService.listerToutes(pageable);
        } else {
            demandes = demandeActeService.listerPourCitoyen(
                    auth.getName(),
                    pageable
            );
        }

        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenir(
            Authentication auth,
            @PathVariable Long id
    ) {
        try {
            DemandeActeDTO dto = demandeActeService.obtenir(
                    id,
                    auth.getName(),
                    estOfficier(auth)
            );

            return ResponseEntity.ok(dto);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();

        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/traitement")
    public ResponseEntity<?> traiter(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody TraitementRequest request
    ) {

        if (!estOfficier(auth)) {
            return ResponseEntity
                    .status(403)
                    .body("Seul un officier peut traiter une demande");
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
        return auth.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_OFFICIER")
        );
    }
}