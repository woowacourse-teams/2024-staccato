package com.staccato.memory.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.s3.service.CloudStorageService;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import com.staccato.memory.repository.MemoryMemberRepository;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryResponses;
import com.staccato.memory.service.dto.response.MomentResponse;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoryService {
    private final MemoryRepository memoryRepository;
    private final MemoryMemberRepository memoryMemberRepository;
    private final MomentRepository momentRepository;
    private final CloudStorageService cloudStorageService;

    @Transactional
    public MemoryIdResponse createMemory(MemoryRequest memoryRequest, MultipartFile thumbnailFile, Member member) {
        Memory memory = memoryRequest.toMemory();
        String thumbnailUrl = uploadFile(thumbnailFile);
        memory.assignThumbnail(thumbnailUrl);
        memory.addMemoryMember(member);
        memoryRepository.save(memory);
        return new MemoryIdResponse(memory.getId());
    }

    public MemoryResponses readAllMemories(Member member, Integer year) {
        return Optional.ofNullable(year)
                .map(y -> readAllByYear(member, y))
                .orElseGet(() -> readAll(member));
    }

    private MemoryResponses readAllByYear(Member member, Integer year) {
        List<MemoryMember> memoryMembers = memoryMemberRepository.findAllByMemberIdAndStartAtYearDesc(member.getId(), year);
        return getMemoryResponses(memoryMembers);
    }

    private MemoryResponses readAll(Member member) {
        List<MemoryMember> memoryMembers = memoryMemberRepository.findAllByMemberIdOrderByMemoryStartAtDesc(member.getId());
        return getMemoryResponses(memoryMembers);
    }

    private MemoryResponses getMemoryResponses(List<MemoryMember> memoryMembers) {
        List<Memory> memories = memoryMembers.stream()
                .map(MemoryMember::getMemory)
                .toList();
        return MemoryResponses.from(memories);
    }

    public MemoryDetailResponse readMemoryById(long memoryId, Member member) {
        Memory memory = getMemoryById(memoryId);
        validateOwner(memory, member);
        List<MomentResponse> momentResponses = getMomentResponses(momentRepository.findAllByMemoryIdOrderByVisitedAt(memoryId));
        return new MemoryDetailResponse(memory, momentResponses);
    }

    private List<MomentResponse> getMomentResponses(List<Moment> moments) {
        return moments.stream()
                .map(moment -> new MomentResponse(moment, getMomentThumbnail(moment)))
                .toList();
    }

    private String getMomentThumbnail(Moment moment) {
        if (moment.hasImage()) {
            return moment.getThumbnailUrl();
        }
        return null;
    }

    @Transactional
    public void updateMemory(MemoryRequest memoryRequest, Long memoryId, MultipartFile thumbnailFile, Member member) {
        Memory updatedMemory = memoryRequest.toMemory();
        Memory originMemory = getMemoryById(memoryId);
        validateOwner(originMemory, member);
        if (!Objects.isNull(thumbnailFile)) {
            String thumbnailUrl = uploadFile(thumbnailFile);
            updatedMemory.assignThumbnail(thumbnailUrl);
        }
        List<Moment> moments = momentRepository.findAllByMemoryIdOrderByVisitedAt(memoryId);
        originMemory.update(updatedMemory, moments);
    }

    private String uploadFile(MultipartFile thumbnailFile) {
        if (Objects.isNull(thumbnailFile)) {
            return null;
        }
        String thumbnailUrl = cloudStorageService.uploadFile(thumbnailFile);

        return thumbnailUrl;
    }

    private Memory getMemoryById(long memoryId) {
        return memoryRepository.findById(memoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteMemory(long memoryId, Member member) {
        memoryRepository.findById(memoryId).ifPresent(memory -> {
            validateOwner(memory, member);
            validateMomentExistsByMemory(memory);
            memoryRepository.deleteById(memoryId);
        });
    }

    private void validateOwner(Memory memory, Member member) {
        if (memory.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }

    private void validateMomentExistsByMemory(Memory memory) {
        if (momentRepository.existsByMemory(memory)) {
            throw new StaccatoException("해당 추억 상세에 순간이 남아있어 삭제할 수 없습니다.");
        }
    }
}
