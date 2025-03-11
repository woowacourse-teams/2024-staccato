package com.staccato.moment.service;

import com.staccato.category.domain.Category;
import com.staccato.moment.service.dto.request.StaccatoRequest;
import com.staccato.moment.service.dto.response.StaccatoDetailResponse;
import com.staccato.moment.service.dto.response.StaccatoIdResponse;
import com.staccato.moment.service.dto.response.StaccatoLocationResponse;
import com.staccato.moment.service.dto.response.StaccatoLocationResponses;
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
import lombok.RequiredArgsConstructor;

@Trace
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StaccatoService {
    private final MomentRepository momentRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final MomentImageRepository momentImageRepository;

    @Transactional
    public StaccatoIdResponse createStaccato(StaccatoRequest staccatoRequest, Member member) {
        Category category = getCategoryById(staccatoRequest.categoryId());
        validateCategoryOwner(category, member);
        Moment moment = staccatoRequest.toStaccato(category);

        momentRepository.save(moment);

        return new StaccatoIdResponse(moment.getId());
    }

    public StaccatoLocationResponses readAllStaccato(Member member) {
        return new StaccatoLocationResponses(momentRepository.findAllByCategory_CategoryMembers_Member(member)
                .stream()
                .map(StaccatoLocationResponse::new).toList());
    }

    public StaccatoDetailResponse readStaccatoById(long staccatoId, Member member) {
        Moment moment = getStaccatoById(staccatoId);
        validateCategoryOwner(moment.getCategory(), member);
        return new StaccatoDetailResponse(moment);
    }

    @Transactional
    public void updateStaccatoById(
            long momentId,
            StaccatoRequest staccatoRequest,
            Member member
    ) {
        Moment moment = getStaccatoById(momentId);
        validateCategoryOwner(moment.getCategory(), member);

        Category targetCategory = getCategoryById(staccatoRequest.categoryId());
        validateCategoryOwner(targetCategory, member);

        Moment newMoment = staccatoRequest.toStaccato(targetCategory);
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

    private Category getCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteStaccatoById(long staccatoId, Member member) {
        momentRepository.findById(staccatoId).ifPresent(moment -> {
            validateCategoryOwner(moment.getCategory(), member);
            commentRepository.deleteAllByMomentIdInBulk(List.of(staccatoId));
            momentImageRepository.deleteAllByMomentIdInBulk(List.of(staccatoId));
            momentRepository.deleteById(staccatoId);
        });
    }

    @Transactional
    public void updateStaccatoFeelingById(long staccatoId, Member member, FeelingRequest feelingRequest) {
        Moment moment = getStaccatoById(staccatoId);
        validateCategoryOwner(moment.getCategory(), member);
        Feeling feeling = feelingRequest.toFeeling();
        moment.changeFeeling(feeling);
    }

    private Moment getStaccatoById(long staccatoId) {
        return momentRepository.findById(staccatoId)
                .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }

    private void validateCategoryOwner(Category category, Member member) {
        if (category.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
