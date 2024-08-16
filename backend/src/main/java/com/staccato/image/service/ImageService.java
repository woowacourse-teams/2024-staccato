package com.staccato.image.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.StaccatoException;
import com.staccato.image.domain.FileExtension;
import com.staccato.image.domain.ImageClient;
import com.staccato.image.service.dto.ImageUrlResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageClient imageClient;

    public List<String> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadFile)
                .toList();
    }

    public ImageUrlResponse uploadFileNew(MultipartFile file) {
        String fileExtension = getFileExtension(file);
        String key = UUID.randomUUID() + fileExtension;
        String contentType = FileExtension.getContentType(fileExtension);
        byte[] fileBytes = getFileBytes(file);

        imageClient.putS3Object(key, contentType, fileBytes);
        String fileUrl = imageClient.getUrl(key);

        return new ImageUrlResponse(fileUrl);
    }

    public String uploadFile(MultipartFile file) {
        String fileExtension = getFileExtension(file);
        String key = UUID.randomUUID() + fileExtension;
        String contentType = FileExtension.getContentType(fileExtension);
        byte[] fileBytes = getFileBytes(file);

        imageClient.putS3Object(key, contentType, fileBytes);

        return imageClient.getUrl(key);
    }

    private byte[] getFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new StaccatoException("전송된 파일이 손상되었거나 지원되지 않는 형식입니다.");
        }
    }

    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}
