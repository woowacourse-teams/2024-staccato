package com.staccato.category.service;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.category.service.dto.request.CategoryReadRequest;
import com.staccato.category.service.dto.request.CategoryRequest;
import com.staccato.category.service.dto.response.CategoryDetailResponse;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryNameResponses;
import com.staccato.category.service.dto.response.CategoryResponses;
import com.staccato.category.service.dto.response.StaccatoResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.comment.CommentFixture;
import com.staccato.fixture.category.CategoryRequestFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.fixture.moment.StaccatoRequestFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.repository.MomentRepository;
import com.staccato.moment.service.StaccatoService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryServiceTest extends ServiceSliceTest {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StaccatoService staccatoService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryMemberRepository categoryMemberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MomentRepository momentRepository;
    @Autowired
    private CommentRepository commentRepository;

    static Stream<Arguments> dateProvider() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 7, 1), 3),
                Arguments.of(LocalDate.of(2024, 7, 2), 2)
        );
    }

    static Stream<Arguments> updateCategoryProvider() {
        return Stream.of(
                Arguments.of(
                        CategoryRequestFixture.create(null, LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)), null),
                Arguments.of(
                        CategoryRequestFixture.create("imageUrl", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10)), "imageUrl"));
    }

    @DisplayName("추억 정보를 기반으로, 추억을 생성하고 작성자를 저장한다.")
    @Test
    void createCategory() {
        // given
        CategoryRequest categoryRequest= CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10));
        Member member = memberRepository.save(MemberFixture.create());

        // when
        categoryService.createCategory(categoryRequest, member);
        CategoryMember categoryMember = categoryMemberRepository.findAllByMemberId(member.getId()).get(0);

        // then
        assertAll(
                () -> assertThat(categoryMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(categoryMember.getCategory().getTitle()).isEqualTo(categoryRequest.categoryTitle())
        );
    }

    @DisplayName("추억의 기간이 null이더라도 추억을 생성할 수 있다.")
    @Test
    void createCategoryWithoutTerm() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixture.create(null, null);
        Member member = memberRepository.save(MemberFixture.create());

        // when
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryRequest, member);
        CategoryMember categoryMember = categoryMemberRepository.findAllByMemberId(member.getId())
                .get(0);

        // then
        assertAll(
                () -> assertThat(categoryMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(categoryMember.getCategory().getId()).isEqualTo(categoryIdResponse.categoryId())
        );
    }

    @DisplayName("사용자의 추억 중 이미 존재하는 추억 이름으로 추억을 생성할 수 없다.")
    @Test
    void cannotCreateCategoryByDuplicatedTitle() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10));
        Member member = memberRepository.save(MemberFixture.create());
        categoryService.createCategory(categoryRequest, member);

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 추억이 있어요. 다른 이름으로 설정해주세요.");
    }

    @DisplayName("다른 사용자의 이미 존재하는 추억 이름으로 추억을 생성할 수 있다.")
    @Test
    void canCreateCategoryByDuplicatedTitleOfOther() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10));
        Member member = memberRepository.save(MemberFixture.create());
        Member otherMember = memberRepository.save(MemberFixture.create("other"));
        categoryService.createCategory(categoryRequest, otherMember);

        // when & then
        assertThatNoException().isThrownBy(() -> categoryService.createCategory(categoryRequest, member));
    }

    @DisplayName("현재 날짜를 포함하는 모든 추억 목록을 조회한다.")
    @MethodSource("dateProvider")
    @ParameterizedTest
    void readAllCategories(LocalDate currentDate, int expectedSize) {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 1), "title1"), member);
        categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 2), "title2"), member);
        categoryService.createCategory(CategoryRequestFixture.create(null, null, "title3"), member);

        // when
        CategoryNameResponses categoryNameResponses = categoryService.readAllCategoriesByDate(member, currentDate);

        // then
        assertThat(categoryNameResponses.categories()).hasSize(expectedSize);
    }

    @DisplayName("CategoryMember 목록을 최근 수정 순으로 조회된다.")
    @Test
    void readAllCategoriesOrderByUpdatedAtDesc() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(null, null, "first"), member);
        categoryService.createCategory(CategoryRequestFixture.create(null, null, "second"), member);
        staccatoService.createStaccato(StaccatoRequestFixture.create(categoryIdResponse.categoryId()), member);
        CategoryReadRequest categoryReadRequest = new CategoryReadRequest("false", null);

        // when
        CategoryResponses categoryResponses = categoryService.readAllCategories(member, categoryReadRequest);

        // then
        assertAll(
                () -> assertThat(categoryResponses.categories().get(0).categoryTitle()).isEqualTo("first"),
                () -> assertThat(categoryResponses.categories().get(1).categoryTitle()).isEqualTo("second")
        );
    }

    @DisplayName("특정 추억을 조회한다.")
    @Test
    void readCategoryById() {
        // given
        Member member = memberRepository.save(MemberFixture.create());

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when
        CategoryDetailResponse categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.mates()).hasSize(1)
        );
    }

    @DisplayName("기간이 없는 특정 추억을 조회한다.")
    @Test
    void readCategoryByIdWithoutTerm() {
        // given
        Member member = memberRepository.save(MemberFixture.create());

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(null, null), member);

        // when
        CategoryDetailResponse categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.mates()).hasSize(1),
                () -> assertThat(categoryDetailResponse.startAt()).isNull(),
                () -> assertThat(categoryDetailResponse.endAt()).isNull()
        );
    }

    @DisplayName("본인 것이 아닌 특정 추억을 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadCategoryByIdIfNotOwner() {
        // given
        Member member = memberRepository.save(MemberFixture.create("member"));
        Member otherMember = memberRepository.save(MemberFixture.create("otherMember"));

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when & then
        assertThatThrownBy(() -> categoryService.readCategoryById(categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 추억을 조회하면 스타카토는 최신순으로 반환한다.")
    @Test
    void readCategoryByIdOrderByVisitedAt() {
        // given
        Member member = memberRepository.save(MemberFixture.create());

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);
        Moment firstMoment = saveMoment(LocalDateTime.of(2023, 7, 1, 10, 0), categoryIdResponse.categoryId());
        Moment secondMoment = saveMoment(LocalDateTime.of(2023, 7, 1, 10, 10), categoryIdResponse.categoryId());
        Moment lastMoment = saveMoment(LocalDateTime.of(2023, 7, 5, 9, 0), categoryIdResponse.categoryId());

        // when
        CategoryDetailResponse categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.staccatos()).hasSize(3),
                () -> assertThat(categoryDetailResponse.staccatos().stream().map(StaccatoResponse::staccatoId).toList())
                        .containsExactly(lastMoment.getId(), secondMoment.getId(), firstMoment.getId())
        );
    }

    @DisplayName("존재하지 않는 추억을 조회하려고 할 경우 예외가 발생한다.")
    @Test
    void failReadMemory() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        long unknownId = 1;

        // when & then
        assertThatThrownBy(() -> categoryService.readCategoryById(unknownId, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 추억을 찾을 수 없어요.");
    }

    @DisplayName("추억 정보를 기반으로, 추억을 수정한다.")
    @MethodSource("updateCategoryProvider")
    @ParameterizedTest
    void updateCategory(CategoryRequest updatedCategory, String expected) {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when
        categoryService.updateCategory(updatedCategory, categoryIdResponse.categoryId(), member);
        Category foundedCategory = categoryRepository.findById(categoryIdResponse.categoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedCategory.getId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(foundedCategory.getTitle()).isEqualTo(updatedCategory.categoryTitle()),
                () -> assertThat(foundedCategory.getDescription()).isEqualTo(updatedCategory.description()),
                () -> assertThat(foundedCategory.getTerm().getStartAt()).isEqualTo(updatedCategory.startAt()),
                () -> assertThat(foundedCategory.getTerm().getEndAt()).isEqualTo(updatedCategory.endAt()),
                () -> assertThat(foundedCategory.getThumbnailUrl()).isEqualTo(expected)
        );
    }

    @DisplayName("기간이 존재하는 추억에 대해 기간이 존재하지 않도록 변경할 수 있다.")
    @Test
    void updateCategoryWithNullableTerm() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when
        CategoryRequest categoryRequest = CategoryRequestFixture.create(null, null);
        categoryService.updateCategory(categoryRequest, categoryIdResponse.categoryId(), member);
        Category foundedCategory = categoryRepository.findById(categoryIdResponse.categoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedCategory.getTerm().getStartAt()).isNull(),
                () -> assertThat(foundedCategory.getTerm().getEndAt()).isNull()
        );
    }

    @DisplayName("존재하지 않는 추억을 수정하려 할 경우 예외가 발생한다.")
    @Test
    void failUpdateCategory() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        CategoryRequest categoryRequest = CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10));

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest, 1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 추억을 찾을 수 없어요.");
    }

    @DisplayName("본인 것이 아닌 추억을 수정하려고 하면 예외가 발생한다.")
    @Test
    void cannotUpdateCategoryIfNotOwner() {
        // given
        Member member = memberRepository.save(MemberFixture.create("member"));
        Member otherMember = memberRepository.save(MemberFixture.create("otherMember"));
        CategoryRequest categoryRequest = CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10));
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest, categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본래 해당 추억의 이름과 동일한 이름으로 추억을 수정할 수 있다.")
    @Test
    void updateCategoryByOriginTitle() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10));
        Member member = memberRepository.save(MemberFixture.create());
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryRequest, member);

        // when & then
        assertThatNoException().isThrownBy(() -> categoryService.updateCategory(categoryRequest, categoryIdResponse.categoryId(), member));
    }

    @DisplayName("이미 존재하는 이름으로 추억을 수정할 수 없다.")
    @Test
    void cannotUpdateCategoryByDuplicatedTitle() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        CategoryRequest categoryRequest1 = CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10), "existingTitle");
        categoryService.createCategory(categoryRequest1, member);
        CategoryRequest categoryRequest2 = CategoryRequestFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10), "otherTitle");
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryRequest2, member);

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest1, categoryIdResponse.categoryId(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 추억이 있어요. 다른 이름으로 설정해주세요.");
    }

    @DisplayName("추억 식별값을 통해 추억을 삭제한다.")
    @Test
    void deleteCategory() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when
        categoryService.deleteCategory(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryRepository.findById(categoryIdResponse.categoryId())).isEmpty(),
                () -> assertThat(categoryMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("추억을 삭제하면 속한 스타카토들도 함께 삭제된다.")
    @Test
    void deleteCategoryWithMoment() {
        // given
        Member member = memberRepository.save(MemberFixture.create());
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);
        Moment moment = saveMoment(LocalDateTime.of(2023, 7, 2, 10, 10), categoryIdResponse.categoryId());
        commentRepository.save(CommentFixture.create(moment, member));

        // when
        categoryService.deleteCategory(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryRepository.findById(categoryIdResponse.categoryId())).isEmpty(),
                () -> assertThat(categoryMemberRepository.findAll()).isEmpty(),
                () -> assertThat(momentRepository.findAll()).isEmpty(),
                () -> assertThat(commentRepository.findAll()).isEmpty()
        );
    }

    private Moment saveMoment(LocalDateTime visitedAt, long memoryId) {
        return momentRepository.save(MomentFixture.create(categoryRepository.findById(memoryId).get(), visitedAt));
    }

    @DisplayName("본인 것이 아닌 추억 상세를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteCategoryIfNotOwner() {
        // given
        Member member = memberRepository.save(MemberFixture.create("member"));
        Member otherMember = memberRepository.save(MemberFixture.create("otherMember"));
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixture.create(LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 10)), member);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }
}
