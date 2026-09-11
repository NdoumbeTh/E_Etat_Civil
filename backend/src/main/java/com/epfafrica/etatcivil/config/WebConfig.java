package com.epfafrica.etatcivil.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.actes-dir:actes}")
    private String actesDir;

    /**
     * Crée les dossiers AVANT que Spring ne calcule leurs URI de ressources statiques.
     * Sans ça, si le dossier n'existe pas encore au démarrage, Path.toUri() ne met pas
     * de slash final, et Spring traite le chemin comme un fichier unique -> 403 sur tout
     * ce qu'il contient, y compris les fichiers ajoutés après coup.
     */
    @PostConstruct
    public void creerDossiers() throws IOException {
        Files.createDirectories(Paths.get(uploadDir).toAbsolutePath().normalize());
        Files.createDirectories(Paths.get(actesDir).toAbsolutePath().normalize());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(localisationAvecSlash(uploadDir));

        registry.addResourceHandler("/actes/**")
                .addResourceLocations(localisationAvecSlash(actesDir));
    }

    private String localisationAvecSlash(String dossier) {
        Path chemin = Paths.get(dossier).toAbsolutePath().normalize();
        String uri = chemin.toUri().toString();
        return uri.endsWith("/") ? uri : uri + "/";
    }
}