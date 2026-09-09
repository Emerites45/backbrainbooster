package com.example.back.domain.storage;

import com.example.back.exception.BusinessException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalFileStorage implements FileStorage {

    private final Path root;

    public LocalFileStorage(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.root = Path.of(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create upload directory: " + this.root, e);
        }
    }

    @Override
    public StoredFile store(String originalFileName, String contentType, InputStream content, long size) {
        String safeName = sanitizeFileName(originalFileName);
        String stored = UUID.randomUUID() + "_" + safeName;
        Path target = root.resolve(stored).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException("Invalid file path");
        }
        try {
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("Failed to store file: " + safeName);
        }
        return new StoredFile(stored, safeName, size, contentType);
    }

    private static String sanitizeFileName(String original) {
        if (original == null || original.isBlank()) {
            return "file.bin";
        }
        String name = Path.of(original).getFileName().toString();
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (name.isBlank()) {
            return "file.bin";
        }
        return name.toLowerCase(Locale.ROOT);
    }
}
