package com.staccato.memory.service;

import com.staccato.memory.service.dto.request.CategoryReadRequest;
import com.staccato.memory.service.dto.request.CategoryRequest;
import com.staccato.memory.service.dto.response.CategoryDetailResponse;
import com.staccato.memory.service.dto.response.CategoryIdResponse;
import com.staccato.memory.service.dto.response.CategoryNameResponses;
import com.staccato.memory.service.dto.response.CategoryResponses;
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
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import com.staccato.memory.repository.MemoryMemberRepository;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentImageRepository;
import com.staccato.moment.repository.MomentRepository;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private static final List<CategoryFilter> DEFAULT_CATEGORY_FILTER = List.of();
    private static final CategorySort DEFAULT_CATEGORY_SORT = CategorySort.UPDATED;

    private final MemoryRepository memoryRepository;
    private final MemoryMemberRepository memoryMemberRepository;
    private final MomentRepository momentRepository;
    private final MomentImageRepository momentImageRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CategoryIdResponse createCategory(CategoryRequest categoryRequest, Member member) {
        Memory memory = categoryRequest.toMemory();
        validateCategoryTitle(memory, member);
        memory.addMemoryMember(member);
        memoryRepository.save(memory);
        return new CategoryIdResponse(memory.getId());
    }

    public CategoryResponses readAllCategories(Member member, CategoryReadRequest categoryReadRequest) {
        List<Memory> rawMemories = getCategories(memoryMemberRepository.findAllByMemberId(member.getId()));
        List<Memory> memories = filterAndSort(rawMemories, categoryReadRequest.getFilters(), categoryReadRequest.getSort());

        return CategoryResponses.from(memories);
    }

    public CategoryNameResponses readAllCategoriesByDate(Member member, LocalDate currentDate) {
        List<Memory> rawMemories = getCategories(memoryMemberRepository.findAllByMemberIdAndDate(member.getId(), currentDate));
        List<Memory> memories = filterAndSort(rawMemories, DEFAULT_CATEGORY_FILTER,
            DEFAULT_CATEGORY_SORT);

        return CategoryNameResponses.from(memories);
    }

    private List<Memory> getCategories(List<MemoryMember> memoryMembers) {
        return memoryMembers.stream().map(MemoryMember::getMemory).collect(Collectors.toList());
    }

    private List<Memory> filterAndSort(List<Memory> memories, List<CategoryFilter> filters, CategorySort sort) {
        for (CategoryFilter filter : filters) {
            memories = filter.apply(memories);
        }
        return sort.apply(memories);
    }

    public CategoryDetailResponse readCategoryById(long memoryId, Member member) {
        Memory memory = getCategoryById(memoryId);
        validateOwner(memory, member);
        List<Moment> moments = momentRepository.findAllByMemoryIdOrdered(memoryId);
        return new CategoryDetailResponse(memory, moments);
    }

    @Transactional
    public void updateCategory(CategoryRequest categoryRequest, Long memoryId, Member member) {
        Memory originMemory = getCategoryById(memoryId);
        validateOwner(originMemory, member);
        Memory updatedMemory = categoryRequest.toMemory();
        if (originMemory.isNotSameTitle(updatedMemory.getTitle())) {
            validateCategoryTitle(updatedMemory, member);
        }
        List<Moment> moments = momentRepository.findAllByMemoryId(memoryId);
        originMemory.update(updatedMemory, moments);
    }

    private Memory getCategoryById(long memoryId) {
        return memoryRepository.findById(memoryId)
                .orElseThrow(() -> new StaccatoException("요청하신 추억을 찾을 수 없어요."));
    }

    private void validateCategoryTitle(Memory memory, Member member) {
        if (memoryMemberRepository.existsByMemberAndMemoryTitle(member, memory.getTitle())) {
            throw new StaccatoException("같은 이름을 가진 추억이 있어요. 다른 이름으로 설정해주세요.");
        }
    }

    @Transactional
    public void deleteCategory(long memoryId, Member member) {
        memoryRepository.findById(memoryId).ifPresent(memory -> {
            validateOwner(memory, member);
            deleteAllRelatedCategory(memoryId);
            memoryRepository.deleteById(memoryId);
        });
    }

    private void validateOwner(Memory memory, Member member) {
        if (memory.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }

    private void deleteAllRelatedCategory(long memoryId) {
        List<Long> momentIds = momentRepository.findAllByMemoryId(memoryId)
                .stream()
                .map(Moment::getId)
                .toList();
        momentImageRepository.deleteAllByMomentIdInBulk(momentIds);
        commentRepository.deleteAllByMomentIdInBulk(momentIds);
        momentRepository.deleteAllByMemoryIdInBulk(memoryId);
        memoryMemberRepository.deleteAllByMemoryIdInBulk(memoryId);
    }
}
