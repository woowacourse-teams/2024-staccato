package com.staccato.image.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.image.service.ImageService;
import com.staccato.image.service.dto.ImageUrlResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Validated
public class ImageController {
    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUrlResponse> uploadFiles(@RequestPart(value = "imageFile") MultipartFile file) {
        ImageUrlResponse imageUrlResponse = imageService.uploadFileNew(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(imageUrlResponse);
    }
}
