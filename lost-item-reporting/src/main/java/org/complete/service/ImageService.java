package org.complete.service;

import lombok.RequiredArgsConstructor;
import org.complete.websocket.AwsS3Service;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AwsS3Service awsS3Service;

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

        try {
            // S3 업로드 및 절대 URL 반환
            return awsS3Service.uploadFile(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 이미지 업로드 실패", e);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    public void deleteFile(String fileUrl) {
        awsS3Service.deleteFile(fileUrl);
    }
}