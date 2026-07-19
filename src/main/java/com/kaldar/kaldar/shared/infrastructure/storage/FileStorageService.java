package com.kaldar.kaldar.shared.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Uploads a file to cloud storage.
     *
     * @param file   the multipart file to upload
     * @param folder the destination folder/path in the cloud bucket (e.g. "cac_documents")
     * @return the public secure URL of the uploaded file
     */
    String uploadFile(MultipartFile file, String folder);
}
