package com.staccato.image.domain;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageExtension {
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpg"),
    JPEG(".jpeg", "image/jpeg"),
    WEBP(".webp", "image/webp");

    private final String extension;
    private final String contentType;

    public static String getContentType(String extension) {
        return Arrays.stream(ImageExtension.values())
                .filter(imageExtension -> imageExtension.getExtension().equalsIgnoreCase(extension))
                .map(ImageExtension::getContentType)
                .findFirst()
                .orElse("application/octet-stream");
    }
}
