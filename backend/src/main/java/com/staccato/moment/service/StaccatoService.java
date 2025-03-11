package com.staccato.moment.service;

import com.staccato.category.domain.Category;
import com.staccato.moment.domain.Staccato;
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
import com.staccato.moment.domain.StaccatoImage;
import com.staccato.moment.repository.StaccatoImageRepository;
import com.staccato.moment.repository.StaccatoRepository;
import com.staccato.moment.service.dto.request.FeelingRequest;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StaccatoService {
    private final StaccatoRepository staccatoRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final StaccatoImageRepository staccatoImageRepository;

    @Transactional
    public StaccatoIdResponse createStaccato(StaccatoRequest staccatoRequest, Member member) {
        Category category = getCategoryById(staccatoRequest.categoryId());
        validateCategoryOwner(category, member);
        Staccato staccato = staccatoRequest.toStaccato(category);

        staccatoRepository.save(staccato);

        return new StaccatoIdResponse(staccato.getId());
    }

    public StaccatoLocationResponses readAllStaccato(Member member) {
        return new StaccatoLocationResponses(staccatoRepository.findAllByCategory_CategoryMembers_Member(member)
                .stream()
                .map(StaccatoLocationResponse::new).toList());
    }

    public StaccatoDetailResponse readStaccatoById(long staccatoId, Member member) {
        Staccato staccato = getStaccatoById(staccatoId);
        validateCategoryOwner(staccato.getCategory(), member);
        return new StaccatoDetailResponse(staccato);
    }

    @Transactional
    public void updateStaccatoById(
            long momentId,
            StaccatoRequest staccatoRequest,
            Member member
    ) {
        Staccato staccato = getStaccatoById(momentId);
        validateCategoryOwner(staccato.getCategory(), member);

        Category targetCategory = getCategoryById(staccatoRequest.categoryId());
        validateCategoryOwner(targetCategory, member);

        Staccato newStaccato = staccatoRequest.toStaccato(targetCategory);
        List<StaccatoImage> existingImages = staccato.existingImages();
        removeExistingImages(existingImages);
        staccato.update(newStaccato);
    }

    private void removeExistingImages(List<StaccatoImage> images) {
        List<Long> ids = images.stream()
                .map(StaccatoImage::getId)
                .toList();
        staccatoImageRepository.deleteAllByIdInBulk(ids);
    }

    private Category getCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteStaccatoById(long staccatoId, Member member) {
        staccatoRepository.findById(staccatoId).ifPresent(moment -> {
            validateCategoryOwner(moment.getCategory(), member);
            commentRepository.deleteAllByStaccatoIdInBulk(List.of(staccatoId));
            staccatoImageRepository.deleteAllByStaccatoIdInBulk(List.of(staccatoId));
            staccatoRepository.deleteById(staccatoId);
        });
    }

    @Transactional
    public void updateStaccatoFeelingById(long staccatoId, Member member, FeelingRequest feelingRequest) {
        Staccato staccato = getStaccatoById(staccatoId);
        validateCategoryOwner(staccato.getCategory(), member);
        Feeling feeling = feelingRequest.toFeeling();
        staccato.changeFeeling(feeling);
    }

    private Staccato getStaccatoById(long staccatoId) {
        return staccatoRepository.findById(staccatoId)
                .orElseThrow(() -> new StaccatoException("요청하신 스타카토를 찾을 수 없어요."));
    }

    private void validateCategoryOwner(Category category, Member member) {
        if (category.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
