package com.staccato.moment.service;

import com.staccato.category.domain.Category;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.moment.domain.Feeling;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImage;
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
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final MomentImageRepository momentImageRepository;

    @Transactional
    public MomentIdResponse createMoment(MomentRequest momentRequest, Member member) {
        Category category = getMemoryById(momentRequest.memoryId());
        validateMemoryOwner(category, member);
        Moment moment = momentRequest.toMoment(category);

        momentRepository.save(moment);

        return new MomentIdResponse(moment.getId());
    }

    public MomentLocationResponses readAllMoment(Member member) {
        return new MomentLocationResponses(momentRepository.findAllByCategory_CategoryMembers_Member(member)
                .stream()
                .map(MomentLocationResponse::new).toList());
    }

    public MomentDetailResponse readMomentById(long momentId, Member member) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getCategory(), member);
        return new MomentDetailResponse(moment);
    }

    @Transactional
    public void updateMomentById(
            long momentId,
            MomentRequest momentRequest,
            Member member
    ) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getCategory(), member);

        Category targetCategory = getMemoryById(momentRequest.memoryId());
        validateMemoryOwner(targetCategory, member);

        Moment newMoment = momentRequest.toMoment(targetCategory);
        List<MomentImage> existingImages = moment.existingImages();
        removeExistingImages(existingImages);
        moment.update(newMoment);
    }

    private void removeExistingImages(List<MomentImage> images) {
        List<Long> ids = images.stream()
                .map(MomentImage::getId)
                .toList();
        momentImageRepository.deleteAllByIdInBulk(ids);
    }

    private Category getMemoryById(long memoryId) {
        return categoryRepository.findById(memoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteMomentById(long momentId, Member member) {
        momentRepository.findById(momentId).ifPresent(moment -> {
            validateMemoryOwner(moment.getCategory(), member);
            commentRepository.deleteAllByMomentIdInBulk(List.of(momentId));
            momentImageRepository.deleteAllByMomentIdInBulk(List.of(momentId));
            momentRepository.deleteById(momentId);
        });
    }

    @Transactional
    public void updateMomentFeelingById(long momentId, Member member, FeelingRequest feelingRequest) {
        Moment moment = getMomentById(momentId);
        validateMemoryOwner(moment.getCategory(), member);
        Feeling feeling = feelingRequest.toFeeling();
        moment.changeFeeling(feeling);
    }

    private Moment getMomentById(long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }

    private void validateMemoryOwner(Category category, Member member) {
        if (category.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
