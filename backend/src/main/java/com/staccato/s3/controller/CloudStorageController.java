package com.staccato.s3.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Size;

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

import com.staccato.s3.service.CloudStorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Validated
public class CloudStorageController {
    private final CloudStorageService cloudStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadFiles(
            @Size(max = 5, message = "사진은 최대 다섯 장까지만 업로드할 수 있어요.") @RequestPart(value = "imageFiles") List<MultipartFile> files
    ) {
        List<String> fileUrls = new ArrayList<>();
        if (!files.isEmpty()) {
            fileUrls = cloudStorageService.uploadFiles(files);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUrls);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFiles(@RequestParam("imageUrls") List<String> urls) {
        cloudStorageService.deleteFiles(urls);
        return ResponseEntity.ok().build();
    }
}
