package com.epfafrica.etatcivil.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public String store(MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String extension = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf('.'));
            }
            String storedName = UUID.randomUUID() + extension;

            Path target = dir.resolve(storedName);
            Files.copy(file.getInputStream(), target);

            return "/uploads/" + storedName;
        } catch (IOException e) {
            throw new RuntimeException("Impossible de stocker le fichier : " + file.getOriginalFilename(), e);
        }
    }
}
