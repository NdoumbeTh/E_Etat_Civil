package com.epfafrica.etatcivil.infrastructure.notification;

import com.epfafrica.etatcivil.domain.model.DemandeActe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void notifierChangementStatut(DemandeActe demande) {
        String destinataire = demande.getCitoyen().getEmail();
        String sujet = "e-ÉtatCivil — Mise à jour de votre demande #" + demande.getId();
        String corps = construireCorps(demande);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinataire);
            message.setSubject(sujet);
            message.setText(corps);
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Échec d'envoi de la notification à {} : {}", destinataire, e.getMessage());
        }
    }

    private String construireCorps(DemandeActe demande) {
        return switch (demande.getStatut()) {
            case VALIDEE -> "Bonjour, votre demande d'acte (" + demande.getTypeActe().getLibelle()
                    + ") a été validée. Vous pouvez télécharger votre acte depuis votre espace.";
            case REJETEE -> "Bonjour, votre demande d'acte (" + demande.getTypeActe().getLibelle()
                    + ") a été rejetée. Motif : " + demande.getMotifRejet();
            default -> "Bonjour, le statut de votre demande d'acte (" + demande.getTypeActe().getLibelle()
                    + ") a évolué : " + demande.getStatut();
        };
    }
}