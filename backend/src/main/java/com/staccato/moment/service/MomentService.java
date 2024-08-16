package com.staccato.moment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.s3.service.CloudStorageService;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImages;
import com.staccato.moment.repository.MomentRepository;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.MomentUpdateRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentIdResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MemoryRepository memoryRepository;
    private final CloudStorageService cloudStorageService;

    @Transactional
    public MomentIdResponse createMoment(MomentRequest momentRequest, Member member) {
        Memory memory = getMemoryById(momentRequest.memoryId());
        validateOwner(memory, member);
        Moment moment = momentRequest.toMoment(memory);

        momentRepository.save(moment);

        return new MomentIdResponse(moment.getId());
    }

    private Memory getMemoryById(long memoryId) {
        return memoryRepository.findById(memoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    public MomentDetailResponse readMomentById(long momentId, Member member) {
        Moment moment = getMomentById(momentId);
        validateOwner(moment.getMemory(), member);
        return new MomentDetailResponse(moment);
    }

    @Transactional
    public void updateMomentById(
            long momentId,
            MomentUpdateRequest momentUpdateRequest,
            List<MultipartFile> momentImageFiles,
            Member member
    ) {
        Moment moment = getMomentById(momentId);
        validateOwner(moment.getMemory(), member);
        List<String> addedImageUrls = cloudStorageService.uploadFiles(momentImageFiles);
        MomentImages momentImages = MomentImages.builder()
                .existingImages(momentUpdateRequest.momentImageUrls())
                .addedImages(addedImageUrls)
                .build();

        moment.update(momentUpdateRequest.placeName(), momentImages);
    }

    private Moment getMomentById(long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new StaccatoException("요청하신 순간 기록을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteMomentById(long momentId, Member member) {
        momentRepository.findById(momentId).ifPresent(moment -> {
            validateOwner(moment.getMemory(), member);
            momentRepository.deleteById(momentId);
        });
    }

    private void validateOwner(Memory memory, Member member) {
        if (memory.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
