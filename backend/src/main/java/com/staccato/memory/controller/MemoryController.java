package com.staccato.memory.controller;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.memory.controller.docs.MemoryControllerDocs;
import com.staccato.memory.service.MemoryService;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryResponses;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/memories")
@RequiredArgsConstructor
public class MemoryController implements MemoryControllerDocs {
    private final MemoryService memoryService;

    @PostMapping
    public ResponseEntity<MemoryIdResponse> createMemory(
            @Valid @RequestBody MemoryRequest memoryRequest,
            @LoginMember Member member
    ) {
        MemoryIdResponse memoryIdResponse = memoryService.createMemory(memoryRequest, member);
        return ResponseEntity.created(URI.create("/memories/" + memoryIdResponse.memoryId())).body(memoryIdResponse);
    }

    @GetMapping
    public ResponseEntity<MemoryResponses> readAllMemories(
            @LoginMember Member member,
            @RequestParam(value = "year", required = false) @Min(value = 1L, message = "년도는 양수로 이루어져야 합니다.") Integer year
    ) {
        MemoryResponses memoryResponses = memoryService.readAllMemories(member, year);
        return ResponseEntity.ok(memoryResponses);
    }

    @GetMapping("/{memoryId}")
    public ResponseEntity<MemoryDetailResponse> readMemory(
            @LoginMember Member member,
            @PathVariable @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId) {
        MemoryDetailResponse memoryDetailResponse = memoryService.readMemoryById(memoryId, member);
        return ResponseEntity.ok(memoryDetailResponse);
    }

    @PutMapping(path = "/{memoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateMemory(
            @PathVariable @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @Valid @RequestPart(value = "data") MemoryRequest memoryRequest,
            @RequestPart(value = "memoryThumbnailFile", required = false) MultipartFile memoryThumbnailFile,
            @LoginMember Member member) {
        memoryService.updateMemory(memoryRequest, memoryId, memoryThumbnailFile, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memoryId}")
    public ResponseEntity<Void> deleteMemory(
            @PathVariable @Min(value = 1L, message = "추억 식별자는 양수로 이루어져야 합니다.") long memoryId,
            @LoginMember Member member) {
        memoryService.deleteMemory(memoryId, member);
        return ResponseEntity.ok().build();
    }
}
