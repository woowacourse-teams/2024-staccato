package com.staccato.s3.controller;

import java.util.List;

import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/captures")
@RequiredArgsConstructor
public class CloudStorageController {
    private final CloudStorageService cloudStorageService;

    @PostMapping
    public ResponseEntity<List<String>> uploadFiles(@RequestPart(value = "imagefiles") List<MultipartFile> files) {
        List<String> fileUrls = cloudStorageService.uploadFiles(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUrls);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFiles(@RequestParam("imageUrls") List<String> urls) {
        cloudStorageService.deleteFiles(urls);
        return ResponseEntity.ok().build();
    }
}
