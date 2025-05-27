package com.staccato.category.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.category.service.dto.request.CategoryColorRequest;
import com.staccato.category.service.dto.request.CategoryCreateRequest;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryStaccatoLocationRangeRequest;
import com.staccato.category.service.dto.request.CategoryUpdateRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponseV3;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryNameResponses;
import com.staccato.category.service.dto.response.CategoryResponseV3;
import com.staccato.category.service.dto.response.CategoryResponsesV3;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponses;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.StaccatoException;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoImageRepository;
import com.staccato.staccato.repository.StaccatoRepository;

import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private static final List<CategoryFilter> DEFAULT_CATEGORY_FILTER = List.of();
    private static final CategorySort DEFAULT_CATEGORY_SORT = CategorySort.UPDATED;

    private final CategoryRepository categoryRepository;
    private final CategoryMemberRepository categoryMemberRepository;
    private final StaccatoRepository staccatoRepository;
    private final StaccatoImageRepository staccatoImageRepository;
    private final CommentRepository commentRepository;
    private final CategoryInvitationRepository categoryInvitationRepository;
    private final CategoryValidator categoryValidator;

    @Transactional
    public CategoryIdResponse createCategory(CategoryCreateRequest categoryCreateRequest, Member member) {
        Category category = categoryCreateRequest.toCategory();
        categoryValidator.validateCategoryTitleAlreadyExists(category, member);
        category.addHost(member);
        categoryRepository.save(category);
        return new CategoryIdResponse(category.getId());
    }

    public CategoryResponsesV3 readAllCategories(Member member, CategoryReadRequest categoryReadRequest) {
        List<Category> rawCategories = getCategories(categoryMemberRepository.findAllByMemberId(member.getId()));
        List<Category> categories = filterAndSort(rawCategories, categoryReadRequest.getFilters(), categoryReadRequest.getSort());
        return getCategoryResponsesV3(categories);
    }

    private CategoryResponsesV3 getCategoryResponsesV3(List<Category> categories) {
        List<CategoryResponseV3> responses = new ArrayList<>();
        for (Category category : categories) {
            long staccatoCount = staccatoRepository.countAllByCategoryId(category.getId());
            responses.add(new CategoryResponseV3(category, staccatoCount));
        }
        return new CategoryResponsesV3(responses);
    }

    public CategoryNameResponses readAllCategoriesByDateAndIsShared(Member member, LocalDate specificDate, boolean isShared) {
        List<Category> rawCategories = getCategories(
                categoryMemberRepository.findAllByMemberIdAndDateAndIsShared(member.getId(), specificDate, isShared));
        List<Category> categories = filterAndSort(rawCategories, DEFAULT_CATEGORY_FILTER,
                DEFAULT_CATEGORY_SORT);

        return CategoryNameResponses.from(categories);
    }

    private List<Category> getCategories(List<CategoryMember> categoryMembers) {
        return categoryMembers.stream().map(CategoryMember::getCategory).collect(Collectors.toList());
    }

    private List<Category> filterAndSort(List<Category> categories, List<CategoryFilter> filters, CategorySort sort) {
        for (CategoryFilter filter : filters) {
            categories = filter.apply(categories);
        }
        return sort.apply(categories);
    }

    public CategoryDetailResponseV3 readCategoryById(long categoryId, Member member) {
        Category category = categoryRepository.findWithCategoryMembersById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 카테고리를 찾을 수 없어요."));
        category.validateReadPermission(member);
        List<Staccato> staccatos = staccatoRepository.findAllByCategoryIdOrdered(categoryId);

        return new CategoryDetailResponseV3(category, staccatos, member);
    }

    public CategoryStaccatoLocationResponses readAllStaccatoByCategory(
            Member member, long categoryId, CategoryStaccatoLocationRangeRequest categoryStaccatoLocationRangeRequest) {
        Category category = categoryValidator.getCategoryByIdOrThrow(categoryId);
        category.validateReadPermission(member);
        List<Staccato> staccatos = staccatoRepository.findByMemberAndLocationRangeAndCategory(
                member,
                categoryStaccatoLocationRangeRequest.swLat(),
                categoryStaccatoLocationRangeRequest.neLat(),
                categoryStaccatoLocationRangeRequest.swLng(),
                categoryStaccatoLocationRangeRequest.neLng(),
                categoryId
        );

        return CategoryStaccatoLocationResponses.of(staccatos);
    }

    @Transactional
    public void updateCategory(CategoryUpdateRequest categoryUpdateRequest, Long categoryId, Member member) {
        Category originCategory = categoryValidator.getCategoryByIdOrThrow(categoryId);
        originCategory.validateModifyPermission(member);

        Category updatedCategory = categoryUpdateRequest.toCategory(originCategory);
        categoryValidator.validateCategoryTitleAlreadyExists(originCategory, updatedCategory, member);

        List<Staccato> staccatos = staccatoRepository.findAllByCategoryId(categoryId);
        originCategory.update(updatedCategory, staccatos);
    }

    @Transactional
    public void updateCategoryColor(long categoryId, CategoryColorRequest categoryColorRequest, Member member) {
        Category category = categoryValidator.getCategoryByIdOrThrow(categoryId);
        category.validateModifyPermission(member);
        category.changeColor(categoryColorRequest.toColor());
    }

    @Transactional
    public void deleteCategory(long categoryId, Member member) {
        Category category = categoryValidator.getCategoryByIdOrThrow(categoryId);
        category.validateModifyPermission(member);
        deleteAllRelatedCategory(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    private void deleteAllRelatedCategory(long categoryId) {
        List<Long> staccatoIds = staccatoRepository.findAllByCategoryId(categoryId)
                .stream()
                .map(Staccato::getId)
                .toList();
        staccatoImageRepository.deleteAllByStaccatoIdInBulk(staccatoIds);
        commentRepository.deleteAllByStaccatoIdInBulk(staccatoIds);
        staccatoRepository.deleteAllByCategoryIdInBulk(categoryId);
        categoryMemberRepository.deleteAllByCategoryIdInBulk(categoryId);
        categoryInvitationRepository.deleteAllByCategoryIdInBulk(categoryId);
    }
}
