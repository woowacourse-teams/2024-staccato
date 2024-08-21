package com.staccato.memory.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
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
import com.staccato.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoryService {
    private final MemoryRepository memoryRepository;
    private final MemoryMemberRepository memoryMemberRepository;
    private final MomentRepository momentRepository;
    private final ImageService imageService;

    @Transactional
    public MemoryIdResponse createMemory(MemoryRequest memoryRequest, Member member) {
        Memory memory = memoryRequest.toMemory();
        memory.addMemoryMember(member);
        memoryRepository.save(memory);
        return new MemoryIdResponse(memory.getId());
    }

    public MemoryResponses readAllMemories(Member member, LocalDate currentDate) {
        return Optional.ofNullable(currentDate)
                .map(d -> readAllMemoriesIncludingDate(member, d))
                .orElseGet(() -> readAll(member));
    }

    private MemoryResponses readAllMemoriesIncludingDate(Member member, LocalDate currentDate) {
        List<MemoryMember> memoryMembers = memoryMemberRepository.findAllByMemberIdAndDateOrderByCreatedAtDesc(member.getId(), currentDate);
        return getMemoryResponses(memoryMembers);
    }

    private MemoryResponses readAll(Member member) {
        List<MemoryMember> memoryMembers = memoryMemberRepository.findAllByMemberIdOrderByMemoryCreatedAtDesc(member.getId());
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
    public void updateMemory(MemoryRequest memoryRequest, Long memoryId, Member member) {
        Memory updatedMemory = memoryRequest.toMemory();
        Memory originMemory = getMemoryById(memoryId);
        validateOwner(originMemory, member);
        List<Moment> moments = momentRepository.findAllByMemoryIdOrderByVisitedAt(memoryId);
        originMemory.update(updatedMemory, moments);
    }

    private Memory getMemoryById(long memoryId) {
        return memoryRepository.findById(memoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteMemory(long memoryId, Member member) {
        memoryRepository.findById(memoryId).ifPresent(memory -> {
            validateOwner(memory, member);
            momentRepository.deleteAllByMemoryId(memoryId);
            memoryRepository.deleteById(memoryId);
        });
    }

    private void validateOwner(Memory memory, Member member) {
        if (memory.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
