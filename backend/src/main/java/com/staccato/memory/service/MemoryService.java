package com.staccato.memory.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryMemberRepository;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.memory.service.dto.request.MemoryRequest;
import com.staccato.memory.service.dto.response.MemoryDetailResponse;
import com.staccato.memory.service.dto.response.MemoryIdResponse;
import com.staccato.memory.service.dto.response.MemoryNameResponses;
import com.staccato.memory.service.dto.response.MemoryResponses;
import com.staccato.memory.service.dto.response.MomentResponse;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentImageRepository;
import com.staccato.moment.repository.MomentRepository;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoryService {
    private final MemoryRepository memoryRepository;
    private final MemoryMemberRepository memoryMemberRepository;
    private final MomentRepository momentRepository;
    private final MomentImageRepository momentImageRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public MemoryIdResponse createMemory(MemoryRequest memoryRequest, Member member) {
        Memory memory = memoryRequest.toMemory();
        validateMemoryTitle(memory, member);
        memory.addMemoryMember(member);
        memoryRepository.save(memory);
        return new MemoryIdResponse(memory.getId());
    }

    public MemoryResponses readAllMemories(Member member) {
        MemoryMembers memoryMembers = new MemoryMembers(memoryMemberRepository.findAllByMemberId(member.getId()));
        memoryMembers.descendByCreatedDate();

        return MemoryResponses.from(memoryMembers.getMemories());
    }

    public MemoryNameResponses readAllMemoriesByDate(Member member, LocalDate currentDate) {
        MemoryMembers memoryMembers = new MemoryMembers(memoryMemberRepository.findAllByMemberIdAndDate(member.getId(), currentDate));
        memoryMembers.descendByCreatedDate();

        return MemoryNameResponses.from(memoryMembers.getMemories());
    }

    public MemoryDetailResponse readMemoryById(long memoryId, Member member) {
        Memory memory = getMemoryById(memoryId);
        validateOwner(memory, member);
        List<MomentResponse> momentResponses = getMomentResponses(momentRepository.findAllByMemoryIdOrdered(memoryId));
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
        Memory originMemory = getMemoryById(memoryId);
        validateOwner(originMemory, member);
        Memory updatedMemory = memoryRequest.toMemory();
        if (originMemory.isNotSameTitle(memoryRequest.memoryTitle())) {
            validateMemoryTitle(updatedMemory, member);
        }
        List<Moment> moments = momentRepository.findAllByMemoryId(memoryId);
        originMemory.update(updatedMemory, moments);
    }

    private Memory getMemoryById(long memoryId) {
        return memoryRepository.findById(memoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    private void validateMemoryTitle(Memory memory, Member member) {
        if (memoryMemberRepository.existsByMemberAndMemoryTitle(member, memory.getTitle())) {
            throw new StaccatoException("같은 이름을 가진 추억이 있어요. 다른 이름으로 설정해주세요.");
        }
    }

    @Transactional
    public void deleteMemory(long memoryId, Member member) {
        memoryRepository.findById(memoryId).ifPresent(memory -> {
            validateOwner(memory, member);
            deleteAllRelatedMemory(memoryId);
            memoryRepository.deleteById(memoryId);
        });
    }

    private void deleteAllRelatedMemory(long memoryId) {
        List<Long> momentIds = momentRepository.findAllByMemoryId(memoryId)
                .stream()
                .map(Moment::getId)
                .toList();
        momentImageRepository.deleteAllByMomentIdInBatch(momentIds);
        commentRepository.deleteAllByMomentIdInBatch(momentIds);
        momentRepository.deleteAllByMemoryIdInBatch(memoryId);
        memoryMemberRepository.deleteAllByMemoryIdInBatch(memoryId);
    }

    private void validateOwner(Memory memory, Member member) {
        if (memory.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
