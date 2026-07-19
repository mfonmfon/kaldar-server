package com.kaldar.kaldar.shared.infrastructure.storage.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kaldar.kaldar.shared.domain.exceptions.FileUploadException;
import com.kaldar.kaldar.shared.infrastructure.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryFileStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    public CloudinaryFileStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        try {
            String publicId = folder + "/" + UUID.randomUUID();
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "kaldar/" + folder,
                            "resource_type", "auto",
                            "overwrite", false
                    )
            );
            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new FileUploadException("Cloudinary did not return a secure URL");
            }
            return secureUrl.toString();
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file to Cloudinary: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File must not be empty");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/pdf") &&
                 !contentType.startsWith("image/"))) {
            throw new FileUploadException(
                    "Only PDF and image files (JPG, PNG) are accepted for document upload");
        }
        // 10 MB guard (Spring also enforces this via yml, belt-and-suspenders)
        if (file.getSize() > 10 * 1024 * 1024L) {
            throw new FileUploadException("File size must not exceed 10 MB");
        }
    }
}
