package com.staccato.moment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Feeling;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.MomentUpdateRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentDetailResponseOldV;
import com.staccato.moment.service.dto.response.MomentIdResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MemoryRepository memoryRepository;

    @Transactional
    public MomentIdResponse createMoment(MomentRequest momentRequest, Member member) {
        Memory memory = getMemoryById(momentRequest.memoryId());
        validateMemoryOwner(memory, member);
        Moment moment = momentRequest.toMoment(memory);

        momentRepository.save(moment);

        return new MomentIdResponse(moment.getId());
    }

    private Memory getMemoryById(long memoryId) {
        return memoryRepository.findById(memoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    public MomentLocationResponses readAllMoment(Member member) {
        return new MomentLocationResponses(momentRepository.findAllByMemory_MemoryMembers_Member(member)
                .stream()
                .map(MomentLocationResponse::new).toList());
    }

    public MomentDetailResponse readMomentById(long momentId, Member member) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getMemory(), member);
        return new MomentDetailResponse(moment);
    }

    public MomentDetailResponseOldV readMomentByIdOldV(long momentId, Member member) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getMemory(), member);
        return new MomentDetailResponseOldV(moment);
    }

    @Transactional
    public void updateMomentById(
            long momentId,
            MomentUpdateRequest momentUpdateRequest,
            Member member
    ) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getMemory(), member);
        moment.update(momentUpdateRequest.staccatoTitle(), momentUpdateRequest.toMomentImages());
    }

    @Transactional
    public void updateMomentByIdV2(
            long momentId,
            MomentRequest momentRequest,
            Member member
    ) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getMemory(), member);

        Memory targetMemory = getMemoryById(momentRequest.memoryId());
        validateMemoryOwner(targetMemory, member);

        Moment updatedMoment = momentRequest.toMoment(targetMemory);
        moment.update(updatedMoment);
    }

    private Moment getMomentById(long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }

    @Transactional
    public void deleteMomentById(long momentId, Member member) {
        momentRepository.findById(momentId).ifPresent(moment -> {
            validateMemoryOwner(moment.getMemory(), member);
            momentRepository.deleteById(momentId);
        });
    }

    @Transactional
    public void updateMomentFeelingById(long momentId, Member member, FeelingRequest feelingRequest) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getMemory(), member);
        Feeling feeling = feelingRequest.toFeeling();
        moment.changeFeeling(feeling);
    }

    private void validateMemoryOwner(Memory memory, Member member) {
        if (memory.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
