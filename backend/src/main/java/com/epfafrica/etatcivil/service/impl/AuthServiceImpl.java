package com.epfafrica.etatcivil.service.impl;

import com.epfafrica.etatcivil.dto.LoginRequest;
import com.epfafrica.etatcivil.dto.LoginResponse;
import com.epfafrica.etatcivil.dto.RegisterRequest;
import com.epfafrica.etatcivil.exception.EmailDejaUtiliseException;
import com.epfafrica.etatcivil.exception.IdentifiantsInvalidesException;
import com.epfafrica.etatcivil.mapper.UtilisateurMapper;
import com.epfafrica.etatcivil.model.Utilisateur;
import com.epfafrica.etatcivil.repository.UtilisateurRepository;
import com.epfafrica.etatcivil.security.JwtService;
import com.epfafrica.etatcivil.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UtilisateurMapper utilisateurMapper;

    public AuthServiceImpl(UtilisateurRepository utilisateurRepository,
                            PasswordEncoder passwordEncoder,
                            JwtService jwtService,
                            UtilisateurMapper utilisateurMapper) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.utilisateurMapper = utilisateurMapper;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        if (utilisateurRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailDejaUtiliseException("Un compte existe déjà avec cet email");
        }

        Utilisateur utilisateur = utilisateurMapper.toEntity(request, passwordEncoder.encode(request.password()));
        utilisateurRepository.save(utilisateur);

        String token = jwtService.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());
        return utilisateurMapper.toLoginResponse(utilisateur, token);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.email()).orElse(null);

        if (utilisateur == null || !passwordEncoder.matches(request.password(), utilisateur.getMotDePasseHash())) {
            throw new IdentifiantsInvalidesException("Identifiants invalides");
        }

        String token = jwtService.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());
        return utilisateurMapper.toLoginResponse(utilisateur, token);
    }
}
