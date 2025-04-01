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
import com.staccato.fixture.category.CategoryRequestFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixture;
import com.staccato.fixture.staccato.StaccatoRequestFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;
import com.staccato.staccato.service.StaccatoService;

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
    private StaccatoRepository staccatoRepository;
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
                Arguments.of(CategoryRequestFixtures.defaultCategoryRequest().withCategoryThumbnailUrl(null).build(), null),
                Arguments.of(CategoryRequestFixtures.defaultCategoryRequest().withCategoryThumbnailUrl("imageUrl").build(), "imageUrl"));
    }

    @DisplayName("카테고리 정보를 기반으로, 카테고리를 생성하고 작성자를 저장한다.")
    @Test
    void createCategory() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixtures.defaultCategoryRequest().build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        // when
        categoryService.createCategory(categoryRequest, member);
        CategoryMember categoryMember = categoryMemberRepository.findAllByMemberId(member.getId()).get(0);

        // then
        assertAll(
                () -> assertThat(categoryMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(categoryMember.getCategory().getTitle()).isEqualTo(categoryRequest.categoryTitle())
        );
    }

    @DisplayName("카테고리의 기간이 null이더라도 카테고리를 생성할 수 있다.")
    @Test
    void createCategoryWithoutTerm() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixtures.defaultCategoryRequest().withTerm(null, null).build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

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

    @DisplayName("사용자의 카테고리 중 이미 존재하는 카테고리 이름으로 카테고리를 생성할 수 없다.")
    @Test
    void cannotCreateCategoryByDuplicatedTitle() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixtures.defaultCategoryRequest().build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        categoryService.createCategory(categoryRequest, member);

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 카테고리가 있어요. 다른 이름으로 설정해주세요.");
    }

    @DisplayName("다른 사용자의 이미 존재하는 카테고리 이름으로 카테고리를 생성할 수 있다.")
    @Test
    void canCreateCategoryByDuplicatedTitleOfOther() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixtures.defaultCategoryRequest().build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember()
                .withNickname("other")
                .buildAndSave(memberRepository);
        categoryService.createCategory(categoryRequest, otherMember);

        // when & then
        assertThatNoException().isThrownBy(() -> categoryService.createCategory(categoryRequest, member));
    }

    @DisplayName("현재 날짜를 포함하는 모든 카테고리 목록을 조회한다.")
    @MethodSource("dateProvider")
    @ParameterizedTest
    void readAllCategories(LocalDate currentDate, int expectedSize) {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        categoryService.createCategory(CategoryRequestFixtures.defaultCategoryRequest()
                .withCategoryTitle("title1")
                .withTerm(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 1))
                .build(), member);
        categoryService.createCategory(CategoryRequestFixtures.defaultCategoryRequest()
                .withCategoryTitle("title2")
                .withTerm(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 2))
                .build(), member);
        categoryService.createCategory(CategoryRequestFixtures.defaultCategoryRequest().withCategoryTitle("title3").withTerm(null, null).build(), member);

        // when
        CategoryNameResponses categoryNameResponses = categoryService.readAllCategoriesByDate(member, currentDate);

        // then
        assertThat(categoryNameResponses.categories()).hasSize(expectedSize);
    }

    @DisplayName("CategoryMember 목록을 최근 수정 순으로 조회된다.")
    @Test
    void readAllCategoriesOrderByUpdatedAtDesc() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryRequestFixtures.defaultCategoryRequest().withCategoryTitle("first").withTerm(null, null).build(), member);
        categoryService.createCategory(
                CategoryRequestFixtures.defaultCategoryRequest().withCategoryTitle("second").withTerm(null, null).build(), member);
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

    @DisplayName("특정 카테고리를 조회한다.")
    @Test
    void readCategoryById() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryRequestFixtures.defaultCategoryRequest().build(), member);

        // when
        CategoryDetailResponse categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.mates()).hasSize(1)
        );
    }

    @DisplayName("기간이 없는 특정 카테고리를 조회한다.")
    @Test
    void readCategoryByIdWithoutTerm() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixtures.defaultCategoryRequest().withTerm(null, null).build(), member);

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

    @DisplayName("본인 것이 아닌 특정 카테고리를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadCategoryByIdIfNotOwner() {
        // given
        Member member = MemberFixtures.defaultMember()
                .withNickname("member")
                .buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember()
                .withNickname("otherMember")
                .buildAndSave(memberRepository);

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixtures.defaultCategoryRequest().build(), member);

        // when & then
        assertThatThrownBy(() -> categoryService.readCategoryById(categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("특정 카테고리를 조회하면 스타카토는 최신순으로 반환한다.")
    @Test
    void readCategoryByIdOrderByVisitedAt() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixtures.defaultCategoryRequest().build(), member);
        Staccato firstStaccato = saveStaccato(LocalDateTime.of(2023, 7, 1, 10, 0), categoryIdResponse.categoryId());
        Staccato secondStaccato = saveStaccato(LocalDateTime.of(2023, 7, 1, 10, 10), categoryIdResponse.categoryId());
        Staccato lastStaccato = saveStaccato(LocalDateTime.of(2023, 7, 5, 9, 0), categoryIdResponse.categoryId());

        // when
        CategoryDetailResponse categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.staccatos()).hasSize(3),
                () -> assertThat(categoryDetailResponse.staccatos().stream().map(StaccatoResponse::staccatoId).toList())
                        .containsExactly(
                            lastStaccato.getId(), secondStaccato.getId(), firstStaccato.getId())
        );
    }

    @DisplayName("존재하지 않는 카테고리를 조회하려고 할 경우 예외가 발생한다.")
    @Test
    void failReadCategory() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        long unknownId = 1;

        // when & then
        assertThatThrownBy(() -> categoryService.readCategoryById(unknownId, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 카테고리를 찾을 수 없어요.");
    }

    @DisplayName("카테고리 정보를 기반으로, 카테고리을 수정한다.")
    @MethodSource("updateCategoryProvider")
    @ParameterizedTest
    void updateCategory(CategoryRequest updatedCategory, String expected) {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixtures.defaultCategoryRequest().build(), member);

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

    @DisplayName("기간이 존재하는 카테고리에 대해 기간이 존재하지 않도록 변경할 수 있다.")
    @Test
    void updateCategoryWithNullableTerm() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixtures.defaultCategoryRequest().build(), member);

        // when
        CategoryRequest categoryRequest = CategoryRequestFixtures.defaultCategoryRequest().withTerm(null, null).build();
        categoryService.updateCategory(categoryRequest, categoryIdResponse.categoryId(), member);
        Category foundedCategory = categoryRepository.findById(categoryIdResponse.categoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedCategory.getTerm().getStartAt()).isNull(),
                () -> assertThat(foundedCategory.getTerm().getEndAt()).isNull()
        );
    }

    @DisplayName("존재하지 않는 카테고리를 수정하려 할 경우 예외가 발생한다.")
    @Test
    void failUpdateCategory() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryRequest categoryRequest = CategoryRequestFixtures.defaultCategoryRequest().build();

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest, 1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 카테고리를 찾을 수 없어요.");
    }

    @DisplayName("본인 것이 아닌 카테고리를 수정하려고 하면 예외가 발생한다.")
    @Test
    void cannotUpdateCategoryIfNotOwner() {
        // given
        Member member = MemberFixtures.defaultMember()
                .withNickname("member")
                .buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember()
                .withNickname("otherMember")
                .buildAndSave(memberRepository);
        CategoryRequest categoryRequest = CategoryRequestFixtures.defaultCategoryRequest().build();
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryRequestFixtures.defaultCategoryRequest().build(), member);

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest, categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본래 해당 카테고리의 이름과 동일한 이름으로 카테고리를 수정할 수 있다.")
    @Test
    void updateCategoryByOriginTitle() {
        // given
        CategoryRequest categoryRequest = CategoryRequestFixtures.defaultCategoryRequest().build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryRequest, member);

        // when & then
        assertThatNoException().isThrownBy(() -> categoryService.updateCategory(categoryRequest, categoryIdResponse.categoryId(), member));
    }

    @DisplayName("이미 존재하는 이름으로 카테고리를 수정할 수 없다.")
    @Test
    void cannotUpdateCategoryByDuplicatedTitle() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryRequest categoryRequest1 = CategoryRequestFixtures.defaultCategoryRequest().withCategoryTitle("existingTitle").build();
        categoryService.createCategory(categoryRequest1, member);
        CategoryRequest categoryRequest2 = CategoryRequestFixtures.defaultCategoryRequest().withCategoryTitle("otherTitle").build();
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryRequest2, member);

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest1, categoryIdResponse.categoryId(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 카테고리가 있어요. 다른 이름으로 설정해주세요.");
    }

    @DisplayName("카테고리 식별값을 통해 카테고리를 삭제한다.")
    @Test
    void deleteCategory() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixtures.defaultCategoryRequest().build(), member);

        // when
        categoryService.deleteCategory(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryRepository.findById(categoryIdResponse.categoryId())).isEmpty(),
                () -> assertThat(categoryMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("카테고리를 삭제하면 속한 스타카토들도 함께 삭제된다.")
    @Test
    void deleteCategoryWithStaccato() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixtures.defaultCategoryRequest().build(), member);
        Staccato staccato = saveStaccato(LocalDateTime.of(2023, 7, 2, 10, 10), categoryIdResponse.categoryId());
        CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(member)
                .buildAndSave(commentRepository);

        // when
        categoryService.deleteCategory(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryRepository.findById(categoryIdResponse.categoryId())).isEmpty(),
                () -> assertThat(categoryMemberRepository.findAll()).isEmpty(),
                () -> assertThat(staccatoRepository.findAll()).isEmpty(),
                () -> assertThat(commentRepository.findAll()).isEmpty()
        );
    }

    private Staccato saveStaccato(LocalDateTime visitedAt, long categoryId) {
        return staccatoRepository.save(
            StaccatoFixture.create(categoryRepository.findById(categoryId).get(), visitedAt));
    }

    @DisplayName("본인 것이 아닌 카테고리 상세를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteCategoryIfNotOwner() {
        // given
        Member member = MemberFixtures.defaultMember()
                .withNickname("member")
                .buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember()
                .withNickname("otherMember")
                .buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
            CategoryRequestFixtures.defaultCategoryRequest().build(), member);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }
}
