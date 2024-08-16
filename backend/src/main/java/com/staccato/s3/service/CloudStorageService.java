package com.staccato.s3.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.StaccatoException;
import com.staccato.s3.domain.CloudStorageClient;
import com.staccato.s3.domain.FileExtension;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudStorageService {
    private static final String TEAM_FOLDER = "staccato/";
    private static final String IMAGE_FOLDER = "image/";

    private final CloudStorageClient cloudStorageClient;

    public List<String> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadFile)
                .toList();
    }

    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String key = makeFilePath(fileName);
        String contentType = getContentType(fileName);
        byte[] fileBytes = getFileBytes(file);

        cloudStorageClient.putS3Object(key, contentType, fileBytes);

        return cloudStorageClient.getUrl(key);
    }

    private byte[] getFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new StaccatoException("전송된 파일이 손상되었거나 지원되지 않는 형식입니다.");
        }
    }

    private String makeFilePath(String fileName) {
        String uniqueFileName = generateUniqueFileName(fileName);
        return TEAM_FOLDER + IMAGE_FOLDER + uniqueFileName;
    }

    private String generateUniqueFileName(String fileName) {
        String fileExtension = getFileExtension(fileName);
        return UUID.randomUUID() + fileExtension;
    }

    private String getContentType(String fileName) {
        String fileExtension = getFileExtension(fileName);
        return FileExtension.getContentType(fileExtension);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex);
    }

    public void deleteFiles(List<String> fileUrls) {
        List<String> objectKeys = fileUrls.stream()
                .map(this::getObjectKeyFromUrl)
                .toList();

        if (fileUrls.size() == 1) {
            cloudStorageClient.deleteS3Object(objectKeys.get(0));
            return;
        }

        cloudStorageClient.deleteMultipleS3Object(objectKeys);
    }

    private String getObjectKeyFromUrl(String fileUrl) {
        URI uri = createUri(fileUrl);
        String path = uri.getPath();

        if (path != null && path.startsWith("/")) {
            return path.substring(1);
        }

        throw new IllegalArgumentException("S3 URL 형식이 올바르지 않습니다.");
    }

    private URI createUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL 형식이 올바르지 않습니다.");
        }
    }
}
