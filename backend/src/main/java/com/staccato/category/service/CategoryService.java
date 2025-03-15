package com.staccato.category.service;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponse;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryNameResponses;
import com.staccato.category.service.dto.response.CategoryResponses;
import com.staccato.staccato.domain.Staccato;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryRepository;
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

    @Transactional
    public CategoryIdResponse createCategory(CategoryRequest categoryRequest, Member member) {
        Category category = categoryRequest.toCategory();
        validateCategoryTitle(category, member);
        category.addCategoryMember(member);
        categoryRepository.save(category);
        return new CategoryIdResponse(category.getId());
    }

    public CategoryResponses readAllCategories(Member member, CategoryReadRequest categoryReadRequest) {
        List<Category> rawMemories = getCategories(categoryMemberRepository.findAllByMemberId(member.getId()));
        List<Category> categories = filterAndSort(rawMemories, categoryReadRequest.getFilters(), categoryReadRequest.getSort());

        return CategoryResponses.from(categories);
    }

    public CategoryNameResponses readAllCategoriesByDate(Member member, LocalDate currentDate) {
        List<Category> rawMemories = getCategories(
            categoryMemberRepository.findAllByMemberIdAndDate(member.getId(), currentDate));
        List<Category> categories = filterAndSort(rawMemories, DEFAULT_CATEGORY_FILTER,
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

    public CategoryDetailResponse readCategoryById(long categoryId, Member member) {
        Category category = getCategoryById(categoryId);
        validateOwner(category, member);
        List<Staccato> staccatos = staccatoRepository.findAllByCategoryIdOrdered(categoryId);
        return new CategoryDetailResponse(category, staccatos);
    }

    @Transactional
    public void updateCategory(CategoryRequest categoryRequest, Long categoryId, Member member) {
        Category originCategory = getCategoryById(categoryId);
        validateOwner(originCategory, member);
        Category updatedCategory = categoryRequest.toCategory();
        if (originCategory.isNotSameTitle(updatedCategory.getTitle())) {
            validateCategoryTitle(updatedCategory, member);
        }
        List<Staccato> staccatos = staccatoRepository.findAllByCategoryId(categoryId);
        originCategory.update(updatedCategory, staccatos);
    }

    private Category getCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 카테고리를 찾을 수 없어요."));
    }

    private void validateCategoryTitle(Category category, Member member) {
        if (categoryMemberRepository.existsByMemberAndCategoryTitle(member, category.getTitle())) {
            throw new StaccatoException("같은 이름을 가진 카테고리가 있어요. 다른 이름으로 설정해주세요.");
        }
    }

    @Transactional
    public void deleteCategory(long categoryId, Member member) {
        categoryRepository.findById(categoryId).ifPresent(category -> {
            validateOwner(category, member);
            deleteAllRelatedCategory(categoryId);
            categoryRepository.deleteById(categoryId);
        });
    }

    private void validateOwner(Category category, Member member) {
        if (category.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
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
    }
}
