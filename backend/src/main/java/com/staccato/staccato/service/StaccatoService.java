package com.staccato.staccato.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Feeling;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.domain.StaccatoImage;
import com.staccato.staccato.repository.StaccatoImageRepository;
import com.staccato.staccato.repository.StaccatoRepository;
import com.staccato.staccato.service.dto.request.FeelingRequest;
import com.staccato.staccato.service.dto.request.StaccatoLocationRangeRequest;
import com.staccato.staccato.service.dto.request.StaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponse;
import com.staccato.staccato.service.dto.response.StaccatoIdResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;
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

    public StaccatoLocationResponsesV2 readAllStaccato(Member member, StaccatoLocationRangeRequest staccatoLocationRangeRequest) {
        List<Staccato> staccatos = staccatoRepository.findByMemberAndLocationRange(
                member,
                staccatoLocationRangeRequest.swLat(),
                staccatoLocationRangeRequest.neLat(),
                staccatoLocationRangeRequest.swLng(),
                staccatoLocationRangeRequest.neLng()
        );

        return StaccatoLocationResponsesV2.of(staccatos);
    }

    public StaccatoDetailResponse readStaccatoById(long staccatoId, Member member) {
        Staccato staccato = getStaccatoById(staccatoId);
        validateCategoryOwner(staccato.getCategory(), member);
        return new StaccatoDetailResponse(staccato);
    }

    @Transactional
    public void updateStaccatoById(
            long staccatoId,
            StaccatoRequest staccatoRequest,
            Member member
    ) {
        Staccato staccato = getStaccatoById(staccatoId);
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
        return categoryRepository.findWithCategoryMembersById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 카테고리를 찾을 수 없어요."));
    }

    @Transactional
    public void deleteStaccatoById(long staccatoId, Member member) {
        staccatoRepository.findById(staccatoId).ifPresent(staccato -> {
            validateCategoryOwner(staccato.getCategory(), member);
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
