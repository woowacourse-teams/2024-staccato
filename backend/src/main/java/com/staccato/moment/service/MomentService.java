package com.staccato.moment.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Feeling;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImage;
import com.staccato.moment.domain.MomentImages;
import com.staccato.moment.repository.MomentImageRepository;
import com.staccato.moment.repository.MomentRepository;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
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
    private final CommentRepository commentRepository;
    private final MomentImageRepository momentImageRepository;

    @Transactional
    public MomentIdResponse createMoment(MomentRequest momentRequest, Member member) {
        Memory memory = getMemoryById(momentRequest.memoryId());
        validateMemoryOwner(memory, member);
        Moment moment = momentRequest.toMoment(memory);
        MomentImages newMomentImages = momentRequest.toMomentImages(moment);

        momentRepository.save(moment);
        momentImageRepository.saveAll(newMomentImages.images());

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
        MomentImages momentImages = getMomentImagesBy(moment);
        return new MomentDetailResponse(moment, momentImages);
    }

    @Transactional
    public void updateMomentById(
            long momentId,
            MomentRequest momentRequest,
            Member member
    ) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getMemory(), member);

        Memory memory = getMemoryById(momentRequest.memoryId());
        validateMemoryOwner(memory, member);

        Moment newMoment = momentRequest.toMoment(memory);
        moment.update(newMoment);

        MomentImages momentImages = getMomentImagesBy(moment);
        MomentImages newMomentImages = momentRequest.toMomentImages(moment);
        removeExistImages(momentImages, newMomentImages);
        saveNewImages(momentImages, newMomentImages);
    }

    private Moment getMomentById(long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }

    private MomentImages getMomentImagesBy(Moment moment) {
        List<MomentImage> momentImages = momentImageRepository.findAllByMomentId(moment.getId())
                .stream()
                .toList();
        return new MomentImages(momentImages);
    }

    private void removeExistImages(MomentImages momentImages, MomentImages newMomentImages) {
        List<MomentImage> removeList = momentImages.findValuesNotPresentIn(newMomentImages);
        List<Long> ids = removeList.stream()
                .map(MomentImage::getId)
                .toList();
        momentImageRepository.deleteAllByIdInBatch(ids);
    }

    private void saveNewImages(MomentImages momentImages, MomentImages newMomentImages) {
        List<MomentImage> saveList = newMomentImages.findValuesNotPresentIn(momentImages);
        momentImageRepository.saveAll(saveList);
    }

    @Transactional
    public void deleteMomentById(long momentId, Member member) {
        momentRepository.findById(momentId).ifPresent(moment -> {
            validateMemoryOwner(moment.getMemory(), member);
            commentRepository.deleteAllByMomentIdInBatch(List.of(momentId));
            momentImageRepository.deleteAllByMomentIdInBatch(List.of(momentId));
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
