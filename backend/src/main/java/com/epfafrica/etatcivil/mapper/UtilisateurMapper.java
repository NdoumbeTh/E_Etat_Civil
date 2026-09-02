package com.epfafrica.etatcivil.mapper;

import com.epfafrica.etatcivil.dto.LoginResponse;
import com.epfafrica.etatcivil.dto.RegisterRequest;
import com.epfafrica.etatcivil.enums.RoleUtilisateur;
import com.epfafrica.etatcivil.model.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    public Utilisateur toEntity(RegisterRequest request, String motDePasseHash) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.nom());
        utilisateur.setEmail(request.email());
        utilisateur.setMotDePasseHash(motDePasseHash);
        utilisateur.setRole(RoleUtilisateur.CITOYEN);
        return utilisateur;
    }

    public LoginResponse toLoginResponse(Utilisateur utilisateur, String token) {
        return new LoginResponse(token, utilisateur.getRole().name());
    }
}
