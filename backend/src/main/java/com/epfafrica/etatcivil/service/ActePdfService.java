package com.epfafrica.etatcivil.service;
import com.epfafrica.etatcivil.model.DemandeActe;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * Génère l'acte PDF numéroté à la validation d'une demande (RG-03).
 * Rendu volontairement simple (texte) — un vrai gabarit officiel
 * pourra être ajouté plus tard sans changer l'appelant.
 */
@Service
public class ActePdfService {

    @Value("${app.actes-dir:actes}")
    private String actesDir;

    public String genererPdf(DemandeActe demande, String numeroUnique) {
        try {
            Path dir = Paths.get(actesDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String nomFichier = numeroUnique + ".pdf";
            Path cible = dir.resolve(nomFichier);

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    float y = 750;
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                    stream.newLineAtOffset(60, y);
                    stream.showText("RÉPUBLIQUE DU SÉNÉGAL — ACTE D'ÉTAT CIVIL");
                    stream.endText();

                    y -= 40;
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    stream.newLineAtOffset(60, y);
                    stream.showText("Type d'acte : " + demande.getTypeActe().getLibelle());
                    stream.endText();

                    y -= 25;
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA, 12);
                    stream.newLineAtOffset(60, y);
                    stream.showText("Numéro unique : " + numeroUnique);
                    stream.endText();

                    y -= 20;
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA, 12);
                    stream.newLineAtOffset(60, y);
                    stream.showText("Délivré le : " + demande.getDateDepot().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    stream.endText();

                    y -= 40;
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    stream.newLineAtOffset(60, y);
                    stream.showText("Informations du demandeur :");
                    stream.endText();

                    y -= 20;
                    for (String ligne : formaterInfos(demande.getInfosDemandeur())) {
                        stream.beginText();
                        stream.setFont(PDType1Font.HELVETICA, 11);
                        stream.newLineAtOffset(70, y);
                        stream.showText(ligne);
                        stream.endText();
                        y -= 16;
                    }

                    y -= 30;
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                    stream.newLineAtOffset(60, y);
                    stream.showText("Document généré automatiquement — e-ÉtatCivil.");
                    stream.endText();
                }

                document.save(cible.toFile());
            }

            return "/actes/" + nomFichier;
        } catch (IOException e) {
            throw new RuntimeException("Impossible de générer le PDF de l'acte", e);
        }
    }

    /** infosDemandeur est un JSON brut ; on l'affiche ligne par ligne sans dépendance JSON supplémentaire. */
    private String[] formaterInfos(String infosJson) {
        return infosJson
                .replaceAll("[{}\"]", "")
                .split(",");
    }
}