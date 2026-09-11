package com.epfafrica.etatcivil.service;

import com.epfafrica.etatcivil.model.DemandeActe;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ActePdfService {

    @Value("${app.actes-dir:actes}")
    private String actesDir;

    private static final PDFont TITRE = PDType1Font.HELVETICA_BOLD;
    private static final PDFont NORMAL = PDType1Font.HELVETICA;
    private static final PDFont NORMAL_GRAS = PDType1Font.HELVETICA_BOLD;
    private static final PDFont ITALIQUE = PDType1Font.HELVETICA_OBLIQUE;

    public String genererPdf(DemandeActe demande, String numeroUnique) {
        try {
            Path dir = Paths.get(actesDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String nomFichier = numeroUnique + ".pdf";
            Path cible = dir.resolve(nomFichier);

            Map<String, String> infos = parserInfos(demande.getInfosDemandeur());
            String libelle = demande.getTypeActe().getLibelle();

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                float largeur = PDRectangle.A4.getWidth();
                float hauteur = PDRectangle.A4.getHeight();
                float marge = 45;
                float milieu = marge + (largeur - 2 * marge) * 0.55f;
                float droite = largeur - marge;

                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

                    float hautEntete = hauteur - 40;
                    float basEntete = hauteur - 180;
                    cs.setLineWidth(0.8f);
                    cs.addRect(marge, basEntete, largeur - 2 * marge, hautEntete - basEntete);
                    cs.stroke();
                    ligneVerticale(cs, milieu, basEntete, hautEntete);
                    ligneHorizontale(cs, marge, milieu, hautEntete - 85);

                    float y = hautEntete - 20;
                    texte(cs, marge + 10, y, NORMAL_GRAS, 9, "RÉGION DE DAKAR");
                    y -= 13;
                    texte(cs, marge + 10, y, NORMAL_GRAS, 9, "DÉPARTEMENT DE DAKAR");
                    y -= 13;
                    texte(cs, marge + 10, y, NORMAL_GRAS, 9, "COMMUNE");

                    texteCentre(cs, milieu, droite, hautEntete - 20, NORMAL_GRAS, 10, "RÉPUBLIQUE DU SÉNÉGAL");
                    texteCentre(cs, milieu, droite, hautEntete - 33, NORMAL, 8, "UN PEUPLE - UN BUT - UNE FOI");
                    texteCentre(cs, milieu, droite, hautEntete - 62, TITRE, 20, "ÉTAT CIVIL");
                    texteCentre(cs, milieu, droite, hautEntete - 100, NORMAL, 9, "AN " + Year.now());
                    texteCentre(cs, milieu, droite, hautEntete - 115, NORMAL, 8, "N° registre : " + numeroUnique);

                    float yTitre = basEntete - 30;
                    texteCentre(cs, marge, droite, yTitre, TITRE, 13, titreDocument(libelle));

                    float hautCorps = yTitre - 15;
                    float basCorps = 160;
                    cs.addRect(marge, basCorps, largeur - 2 * marge, hautCorps - basCorps);
                    cs.stroke();

                    float yCorps = hautCorps - 25;

                    if ("MARIAGE".equals(libelle)) {
                        texteCentre(cs, marge, droite, yCorps, NORMAL_GRAS, 11, "Acte : " + numeroUnique + " / " + Year.now());
                        yCorps -= 30;
                    } else {
                        texte(cs, marge + 12, yCorps, NORMAL, 9,
                                "Sur l'année " + Year.now() + ", il a été dressé l'acte suivant :");
                        yCorps -= 30;
                    }

                    switch (libelle) {
                        case "NAISSANCE" -> corpsNaissance(cs, infos, marge, droite, yCorps);
                        case "MARIAGE" -> corpsMariage(cs, infos, marge, droite, yCorps);
                        case "DECES" -> corpsDeces(cs, infos, marge, droite, yCorps);
                        default -> corpsGenerique(cs, infos, marge, droite, yCorps);
                    }

                    float yPied = basCorps - 30;
                    texte(cs, marge, yPied, NORMAL, 8,
                            "Délivré le " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    texte(cs, marge, yPied - 14, NORMAL, 8, "Numéro unique : " + numeroUnique);
                    texte(cs, marge, yPied - 34, ITALIQUE, 8, "Document généré automatiquement — e-ÉtatCivil (usage pédagogique, non officiel).");
                }

                document.save(cible.toFile());
            }

            return "/actes/" + nomFichier;
        } catch (IOException e) {
            throw new RuntimeException("Impossible de générer le PDF de l'acte", e);
        }
    }

    private String titreDocument(String libelle) {
        return switch (libelle) {
            case "NAISSANCE" -> "EXTRAIT DU REGISTRE DES ACTES DE NAISSANCE";
            case "MARIAGE" -> "CERTIFICAT DE MARIAGE CONSTATÉ";
            case "DECES" -> "CERTIFICAT DE DÉCÈS";
            default -> "EXTRAIT D'ACTE D'ÉTAT CIVIL";
        };
    }

    private void corpsNaissance(PDPageContentStream cs, Map<String, String> infos, float gauche, float droite, float y) throws IOException {
        float milieu = gauche + (droite - gauche) / 2;
        champ(cs, gauche + 12, y, "Nom complet de l'enfant", infos.get("nomEnfant"));
        y -= 30;
        champ(cs, gauche + 12, y, "Date de naissance", infos.get("dateNaissance"));
        champ(cs, milieu, y, "Lieu de naissance", infos.get("lieuNaissance"));
        y -= 30;
        champ(cs, gauche + 12, y, "Nom du père", infos.get("nomPere"));
        y -= 30;
        champ(cs, gauche + 12, y, "Nom de la mère", infos.get("nomMere"));
    }

    private void corpsMariage(PDPageContentStream cs, Map<String, String> infos, float gauche, float droite, float y) throws IOException {
        float largeurMax = droite - gauche - 24;
        String epoux = valeurOuTiret(infos.get("nomEpoux"));
        String epouse = valeurOuTiret(infos.get("nomEpouse"));
        String date = valeurOuTiret(infos.get("dateMariage"));
        String lieu = valeurOuTiret(infos.get("lieuMariage"));

        y = texteMultiligne(cs,
                "Nous soussigné, Officier d'état civil, certifions avoir célébré et constaté le mariage contracté entre :",
                gauche + 12, y, largeurMax, NORMAL, 10, 14);
        y -= 8;

        texte(cs, gauche + 12, y, NORMAL_GRAS, 11, "Époux : " + epoux);
        y -= 20;
        texte(cs, gauche + 12, y, NORMAL_GRAS, 11, "Épouse : " + epouse);
        y -= 26;

        y = texteMultiligne(cs, "Mariage célébré le " + date + " à " + lieu + ".",
                gauche + 12, y, largeurMax, NORMAL, 10, 14);
        y -= 12;

        texteMultiligne(cs,
                "En foi de quoi, nous délivrons le présent certificat de mariage pour servir et valoir ce que de droit.",
                gauche + 12, y, largeurMax, NORMAL, 10, 14);
    }

    private void corpsDeces(PDPageContentStream cs, Map<String, String> infos, float gauche, float droite, float y) throws IOException {
        float milieu = gauche + (droite - gauche) / 2;
        champ(cs, gauche + 12, y, "Nom et prénom du défunt", infos.get("nomDefunt"));
        y -= 30;
        champ(cs, gauche + 12, y, "Date du décès", infos.get("dateDeces"));
        champ(cs, milieu, y, "Commune / lieu du décès", infos.get("lieuDeces"));
        y -= 30;
        champ(cs, gauche + 12, y, "Déclarant", infos.get("nomDeclarant"));
    }

    private void corpsGenerique(PDPageContentStream cs, Map<String, String> infos, float gauche, float droite, float y) throws IOException {
        for (Map.Entry<String, String> entree : infos.entrySet()) {
            champ(cs, gauche + 12, y, entree.getKey(), entree.getValue());
            y -= 25;
        }
    }

    private void champ(PDPageContentStream cs, float x, float y, String label, String valeur) throws IOException {
        texte(cs, x, y, NORMAL, 8, label.toUpperCase());
        texte(cs, x, y - 14, NORMAL_GRAS, 11, valeurOuTiret(valeur));
    }

    private String valeurOuTiret(String v) {
        return v != null && !v.isBlank() ? v : "—";
    }

    private Map<String, String> parserInfos(String infosJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(infosJson, LinkedHashMap.class);
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private void texte(PDPageContentStream cs, float x, float y, PDFont police, float taille, String contenu) throws IOException {
        cs.beginText();
        cs.setFont(police, taille);
        cs.newLineAtOffset(x, y);
        cs.showText(contenu);
        cs.endText();
    }

    private void texteCentre(PDPageContentStream cs, float gauche, float droite, float y, PDFont police, float taille, String contenu) throws IOException {
        float largeurTexte = police.getStringWidth(contenu) / 1000 * taille;
        float x = gauche + ((droite - gauche) - largeurTexte) / 2;
        texte(cs, x, y, police, taille, contenu);
    }

    private float texteMultiligne(PDPageContentStream cs, String texteComplet, float x, float yDepart, float largeurMax, PDFont police, float taille, float interligne) throws IOException {
        String[] mots = texteComplet.split(" ");
        StringBuilder ligne = new StringBuilder();
        float y = yDepart;
        for (String mot : mots) {
            String essai = ligne.length() == 0 ? mot : ligne + " " + mot;
            float largeur = police.getStringWidth(essai) / 1000 * taille;
            if (largeur > largeurMax && ligne.length() > 0) {
                texte(cs, x, y, police, taille, ligne.toString());
                y -= interligne;
                ligne = new StringBuilder(mot);
            } else {
                ligne = new StringBuilder(essai);
            }
        }
        if (ligne.length() > 0) {
            texte(cs, x, y, police, taille, ligne.toString());
            y -= interligne;
        }
        return y;
    }

    private void ligneVerticale(PDPageContentStream cs, float x, float yBas, float yHaut) throws IOException {
        cs.moveTo(x, yBas);
        cs.lineTo(x, yHaut);
        cs.stroke();
    }

    private void ligneHorizontale(PDPageContentStream cs, float xGauche, float xDroite, float y) throws IOException {
        cs.moveTo(xGauche, y);
        cs.lineTo(xDroite, y);
        cs.stroke();
    }
}