package com.epfafrica.etatcivil.config;

import com.epfafrica.etatcivil.enums.RoleUtilisateur;
import com.epfafrica.etatcivil.model.TypeActe;
import com.epfafrica.etatcivil.model.Utilisateur;
import com.epfafrica.etatcivil.repository.TypeActeRepository;
import com.epfafrica.etatcivil.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Jeu de données minimal pour démontrer login -> JWT -> route protégée en Séance 2.
 * Mot de passe pour tous les comptes de démo : "password123".
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final TypeActeRepository typeActeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UtilisateurRepository utilisateurRepository,
                       TypeActeRepository typeActeRepository,
                       PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.typeActeRepository = typeActeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (utilisateurRepository.count() == 0) {
            seedUtilisateur("citoyen@demo.sn", "Fatou Citoyenne", RoleUtilisateur.CITOYEN);
            seedUtilisateur("officier@demo.sn", "Moussa Officier", RoleUtilisateur.OFFICIER);
            seedUtilisateur("chef@demo.sn", "Awa ChefService", RoleUtilisateur.CHEF_SERVICE);
            seedUtilisateur("admin@demo.sn", "Admin Systeme", RoleUtilisateur.ADMIN);
        }

        if (typeActeRepository.count() == 0) {
            seedTypeActe("NAISSANCE", "Certificat de naissance du demandeur, pièce d'identité");
            seedTypeActe("MARIAGE", "Livret de famille, pièces d'identité des époux");
            seedTypeActe("DECES", "Certificat médical de décès, pièce d'identité du déclarant");
        }
    }

    private void seedUtilisateur(String email, String nom, RoleUtilisateur role) {
        Utilisateur u = new Utilisateur();
        u.setEmail(email);
        u.setNom(nom);
        u.setRole(role);
        u.setMotDePasseHash(passwordEncoder.encode("password123"));
        utilisateurRepository.save(u);
    }

    private void seedTypeActe(String libelle, String piecesRequises) {
        TypeActe t = new TypeActe();
        t.setLibelle(libelle);
        t.setPiecesRequises(piecesRequises);
        typeActeRepository.save(t);
    }
}
