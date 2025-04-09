package org.complete.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    public String uploadImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty() || imageFile.getOriginalFilename() == null) {
            return null;
        }

        String[] allowedExtensions = { "jpg", "jpeg", "png" };
        String fileExtension = getFileExtension(imageFile.getOriginalFilename());
        boolean isValidExtension = false;

        for (String ext : allowedExtensions) {
            if (fileExtension.equalsIgnoreCase(ext)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file type. Only jpg, jpeg, png are allowed.");
        }

        long maxSize = 5 * 1024 * 1024;
        if (imageFile.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds the maximum limit of 5MB.");
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path uploadDirPath = Paths.get("uploads/images");
        Path filePath = uploadDirPath.resolve(fileName);

        try {
            Files.createDirectories(uploadDirPath);
            imageFile.transferTo(filePath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save the image file.", e);
        }

        return "/images/" + fileName;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
}