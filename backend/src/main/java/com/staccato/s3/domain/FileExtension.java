package com.staccato.s3.domain;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileExtension {
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpg"),
    JPEG(".jpeg", "image/jpeg"),
    WEBP(".webp", "image/webp");

    private final String extension;
    private final String contentType;

    public static String getContentType(String extension) {
        return Arrays.stream(FileExtension.values())
                .filter(fileExtension -> fileExtension.getExtension().equalsIgnoreCase(extension))
                .map(FileExtension::getContentType)
                .findFirst()
                .orElse("multipart/form-data");
    }
}
