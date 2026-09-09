package com.example.back.domain.storage;

import java.io.InputStream;

/**
 * Port de stockage de fichiers (OCP) — implémentation locale pour le MVP.
 */
public interface FileStorage {

    StoredFile store(String originalFileName, String contentType, InputStream content, long size);

    record StoredFile(String relativePath, String storedFileName, long size, String contentType) {
    }
}
