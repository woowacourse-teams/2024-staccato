package com.staccato.s3.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.s3.service.CloudStorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class CloudStorageController {
    private final CloudStorageService cloudStorageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("file") List<MultipartFile> images) {
        List<String> fileUrl = cloudStorageService.uploadFiles(images);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUrl);
    }
}
