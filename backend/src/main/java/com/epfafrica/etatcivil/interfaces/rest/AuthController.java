package com.epfafrica.etatcivil.interfaces.rest;

import com.epfafrica.etatcivil.domain.enums.RoleUtilisateur;
import com.epfafrica.etatcivil.domain.model.Utilisateur;
import com.epfafrica.etatcivil.infrastructure.persistence.UtilisateurRepository;
import com.epfafrica.etatcivil.infrastructure.security.JwtService;
import com.epfafrica.etatcivil.interfaces.dto.LoginRequest;
import com.epfafrica.etatcivil.interfaces.dto.RegisterRequest;
import com.epfafrica.etatcivil.interfaces.dto.LoginResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    if (utilisateurRepository.findByEmail(request.email()).isPresent()) {
        return ResponseEntity.status(409).body("Un compte existe déjà avec cet email");
    }

    Utilisateur utilisateur = new Utilisateur();
    utilisateur.setNom(request.nom());
    utilisateur.setEmail(request.email());
    utilisateur.setMotDePasseHash(passwordEncoder.encode(request.password()));
    utilisateur.setRole(RoleUtilisateur.CITOYEN);
    utilisateurRepository.save(utilisateur);

    String token = jwtService.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());
    return ResponseEntity.ok(new LoginResponse(token, utilisateur.getRole().name()));
}
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.email())
                .orElse(null);

        if (utilisateur == null || !passwordEncoder.matches(request.password(), utilisateur.getMotDePasseHash())) {
            return ResponseEntity.status(401).body("Identifiants invalides");
        }

        String token = jwtService.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, utilisateur.getRole().name()));
    }
}
