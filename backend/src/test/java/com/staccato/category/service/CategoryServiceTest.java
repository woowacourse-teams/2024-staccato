package com.staccato.category.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.domain.Color;
import com.staccato.category.domain.Role;
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
import com.staccato.category.service.dto.response.CategoryResponsesV3;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponse;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponses;
import com.staccato.category.service.dto.response.StaccatoResponse;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryCreateRequestFixtures;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.category.CategoryUpdateRequestFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.fixture.staccato.StaccatoRequestFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.InvitationService;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
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
    private InvitationService invitationService;
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
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;

    static Stream<Arguments> dateProvider() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 6, 1), 3),
                Arguments.of(LocalDate.of(2024, 6, 2), 2)
        );
    }

    static Stream<Arguments> updateCategoryProvider() {
        return Stream.of(
                Arguments.of(CategoryUpdateRequestFixtures.defaultCategoryUpdateRequest()
                        .withCategoryThumbnailUrl(null).build(), null),
                Arguments.of(CategoryUpdateRequestFixtures.defaultCategoryUpdateRequest()
                        .withCategoryThumbnailUrl("https://example.com/categoryThumbnailUrl.jpg")
                        .build(), "https://example.com/categoryThumbnailUrl.jpg"));
    }

    @DisplayName("카테고리 정보를 기반으로, 카테고리를 생성하고 작성자를 저장한다.")
    @Test
    void createCategory() {
        // given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        // when
        categoryService.createCategory(categoryCreateRequest, member);
        CategoryMember categoryMember = categoryMemberRepository.findAllByMemberId(member.getId()).get(0);

        // then
        assertAll(
                () -> assertThat(categoryMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(categoryMember.getCategory()
                        .getTitle().getTitle()).isEqualTo(categoryCreateRequest.categoryTitle())
        );
    }

    @DisplayName("카테고리의 기간이 null이더라도 카테고리를 생성할 수 있다.")
    @Test
    void createCategoryWithoutTerm() {
        // given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .withTerm(null, null).build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        // when
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryCreateRequest, member);
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
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        categoryService.createCategory(categoryCreateRequest, member);

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(categoryCreateRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 카테고리가 있어요. 다른 이름으로 설정해주세요.");
    }

    @DisplayName("다른 사용자의 이미 존재하는 카테고리 이름으로 카테고리를 생성할 수 있다.")
    @Test
    void canCreateCategoryByDuplicatedTitleOfOther() {
        // given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().withNickname("otherMem").buildAndSave(memberRepository);
        categoryService.createCategory(categoryCreateRequest, otherMember);

        // when & then
        assertThatNoException().isThrownBy(() -> categoryService.createCategory(categoryCreateRequest, member));
    }

    @DisplayName("특정 날짜를 포함하는 모든 카테고리 목록을 조회한다.")
    @MethodSource("dateProvider")
    @ParameterizedTest
    void readAllCategories(LocalDate specificDate, int expectedSize) {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        categoryService.createCategory(CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .withCategoryTitle("first")
                .withTerm(LocalDate.of(2024, 6, 1),
                        LocalDate.of(2024, 6, 1)).build(), member);
        categoryService.createCategory(CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .withCategoryTitle("second")
                .withTerm(LocalDate.of(2024, 6, 1)
                        , LocalDate.of(2024, 6, 2)).build(), member);
        categoryService.createCategory(CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .withCategoryTitle("third")
                .withTerm(null, null).build(), member);

        // when
        CategoryNameResponses categoryNameResponses = categoryService.readAllCategoriesByDateAndIsShared(member, specificDate, false);

        // then
        assertThat(categoryNameResponses.categories()).hasSize(expectedSize);
    }

    @DisplayName("CategoryMember 목록을 최근 수정 순으로 조회된다.")
    @Test
    void readAllCategoriesOrderByUpdatedAtDesc() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                        .withCategoryTitle("first").build(), member);
        categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                        .withCategoryTitle("second").build(), member);
        staccatoService.createStaccato(StaccatoRequestFixtures.defaultStaccatoRequest()
                .withCategoryId(categoryIdResponse.categoryId()).build(), member);
        CategoryReadRequest categoryReadRequest = new CategoryReadRequest("false", null);

        // when
        CategoryResponsesV3 categoryResponses = categoryService.readAllCategories(member, categoryReadRequest);

        // then
        assertAll(
                () -> assertThat(categoryResponses.categories().get(0).categoryTitle()).isEqualTo("first"),
                () -> assertThat(categoryResponses.categories().get(1).categoryTitle()).isEqualTo("second")
        );
    }

    @DisplayName("HOST는 본인이 속한 특정 카테고리를 조회할 수 있다.")
    @Test
    void readCategoryByIdByHost() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), host);

        // when
        CategoryDetailResponseV3 categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), host);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.members()).hasSize(1)
        );
    }

    @DisplayName("GUEST는 본인이 속한 특정 카테고리를 조회할 수 있다.")
    @Test
    void readCategoryByIdByGuest() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), host);
        Category category = categoryRepository.findById(categoryIdResponse.categoryId()).get();
        categoryMemberRepository.save(CategoryMember.builder().category(category).member(guest).role(Role.GUEST)
                .build());

        // when
        CategoryDetailResponseV3 categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), host);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.members()).hasSize(2)
        );
    }

    @DisplayName("기간이 없는 특정 카테고리를 조회한다.")
    @Test
    void readCategoryByIdWithoutTerm() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().withTerm(null, null).build(), member);

        // when
        CategoryDetailResponseV3 categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.members()).hasSize(1),
                () -> assertThat(categoryDetailResponse.startAt()).isNull(),
                () -> assertThat(categoryDetailResponse.endAt()).isNull()
        );
    }

    @DisplayName("본인 것이 아닌 특정 카테고리를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadCategoryByIdIfNotOwner() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().withNickname("otherMem").buildAndSave(memberRepository);

        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), member);

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
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), member);
        Category category = categoryRepository.findById(categoryIdResponse.categoryId()).get();
        Staccato firstStaccato = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 1, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato secondStaccato = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 2, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato thirdStaccato = StaccatoFixtures.defaultStaccato()
                .withVisitedAt(LocalDateTime.of(2024, 6, 3, 0, 0))
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        CategoryDetailResponseV3 categoryDetailResponse = categoryService.readCategoryById(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryDetailResponse.categoryId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(categoryDetailResponse.staccatos()).hasSize(3),
                () -> assertThat(categoryDetailResponse.staccatos().stream().map(StaccatoResponse::staccatoId).toList())
                        .containsExactly(
                                thirdStaccato.getId(), secondStaccato.getId(), firstStaccato.getId())
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

    @DisplayName("위경도 조건이 없으면 카테고리에 속한 모든 스타카토 목록을 조회한다.")
    @Test
    void readAllStaccato() {
        // given
        Member member = MemberFixtures.defaultMember().withCode("me").buildAndSave(memberRepository);
        Member member2 = MemberFixtures.defaultMember().withNickname("other").buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withColor(Color.BLUE)
                .withHost(member)
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withTitle("title2")
                .withColor(Color.PINK)
                .withHost(member2)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Staccato otherStaccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category2).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        CategoryStaccatoLocationResponses responses = categoryService.readAllStaccatoByCategory(
                member, category.getId(), new CategoryStaccatoLocationRangeRequest(null, null, null, null));

        // then
        assertAll(
                () -> assertThat(responses.categoryStaccatoLocationResponses()).hasSize(2),
                () -> assertThat(responses.categoryStaccatoLocationResponses())
                        .containsExactlyInAnyOrder(
                                new CategoryStaccatoLocationResponse(staccato),
                                new CategoryStaccatoLocationResponse(staccato2)
                        )
                        .doesNotContain(new CategoryStaccatoLocationResponse(otherStaccato))
        );
    }

    @DisplayName("카테고리 목록 조회 시 각 카테고리에 해당하는 스타카토 개수가 포함된다.")
    @Test
    void readAllCategoriesContainsStaccatoCount() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(member)
                .buildAndSave(categoryRepository);

        StaccatoFixtures.defaultStaccato().withCategory(category).buildAndSave(staccatoRepository);
        StaccatoFixtures.defaultStaccato().withCategory(category).buildAndSave(staccatoRepository);

        CategoryReadRequest request = new CategoryReadRequest(null, null);

        // when
        CategoryResponsesV3 categoryResponses = categoryService.readAllCategories(member, request);

        // then
        assertAll(
                () -> assertThat(categoryResponses.categories()).hasSize(1),
                () -> assertThat(categoryResponses.categories().get(0).staccatoCount()).isEqualTo(2L)
        );
    }

    @DisplayName("카테고리 정보를 기반으로, 카테고리를 수정한다.")
    @MethodSource("updateCategoryProvider")
    @ParameterizedTest
    void updateCategory(CategoryUpdateRequest categoryUpdateRequest, String expected) {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), member);

        // when
        categoryService.updateCategory(categoryUpdateRequest, categoryIdResponse.categoryId(), member);
        Category foundedCategory = categoryRepository.findById(categoryIdResponse.categoryId()).get();

        // then
        assertAll(
                () -> assertThat(foundedCategory.getId()).isEqualTo(categoryIdResponse.categoryId()),
                () -> assertThat(foundedCategory.getTitle()
                        .getTitle()).isEqualTo(categoryUpdateRequest.categoryTitle()),
                () -> assertThat(foundedCategory.getDescription()
                        .getDescription()).isEqualTo(categoryUpdateRequest.description()),
                () -> assertThat(foundedCategory.getTerm().getStartAt()).isEqualTo(categoryUpdateRequest.startAt()),
                () -> assertThat(foundedCategory.getTerm().getEndAt()).isEqualTo(categoryUpdateRequest.endAt()),
                () -> assertThat(foundedCategory.getThumbnailUrl()).isEqualTo(expected)
        );
    }

    @DisplayName("기간이 존재하는 카테고리에 대해 기간이 존재하지 않도록 변경할 수 있다.")
    @Test
    void updateCategoryWithNullableTerm() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), member);

        // when
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.defaultCategoryUpdateRequest()
                .withTerm(null, null).build();
        categoryService.updateCategory(categoryUpdateRequest, categoryIdResponse.categoryId(), member);
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
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.defaultCategoryUpdateRequest()
                .build();

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryUpdateRequest, 1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 카테고리를 찾을 수 없어요.");
    }

    @DisplayName("본인 것이 아닌 카테고리를 수정하려고 하면 예외가 발생한다.")
    @Test
    void cannotUpdateCategoryIfNotOwner() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().withNickname("otherMem").buildAndSave(memberRepository);
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.defaultCategoryUpdateRequest()
                .build();
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), member);

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryUpdateRequest, categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("카테고리의 색상을 변경한다.")
    @Test
    void updateCategoryColor() {
        // given
        CategoryColorRequest categoryColorRequest = new CategoryColorRequest(Color.PINK.getName());
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .build();
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryCreateRequest, member);

        // when
        categoryService.updateCategoryColor(categoryIdResponse.categoryId(), categoryColorRequest, member);

        // then
        assertThat(categoryRepository.findById(categoryIdResponse.categoryId()).get().getColor()).isEqualTo(Color.PINK);
    }

    @DisplayName("본래 해당 카테고리의 이름과 동일한 이름으로 카테고리를 수정할 수 있다.")
    @Test
    void updateCategoryByOriginTitle() {
        // given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .withCategoryTitle("title").build();
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.defaultCategoryUpdateRequest()
                .withCategoryTitle("title").build();
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryCreateRequest, member);

        // when & then
        assertThatNoException().isThrownBy(() -> categoryService.updateCategory(categoryUpdateRequest, categoryIdResponse.categoryId(), member));
    }

    @DisplayName("이미 존재하는 이름으로 카테고리를 수정할 수 없다.")
    @Test
    void cannotUpdateCategoryByDuplicatedTitle() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        CategoryCreateRequest existingTitleRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .withCategoryTitle("existingTitle").build();
        categoryService.createCategory(existingTitleRequest, member);

        CategoryCreateRequest editableCategoryRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .withCategoryTitle("otherTitle").build();
        CategoryIdResponse editableCategoryId = categoryService.createCategory(editableCategoryRequest, member);

        // when & then
        CategoryUpdateRequest updateToDuplicateTitleRequest = CategoryUpdateRequestFixtures.defaultCategoryUpdateRequest()
                .withCategoryTitle(existingTitleRequest.categoryTitle()).build();

        assertThatThrownBy(() -> categoryService.updateCategory(updateToDuplicateTitleRequest, editableCategoryId.categoryId(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 카테고리가 있어요. 다른 이름으로 설정해주세요.");
    }

    @DisplayName("카테고리 식별값을 통해 카테고리를 삭제한다.")
    @Test
    void deleteCategory() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), member);

        // when
        categoryService.deleteCategory(categoryIdResponse.categoryId(), member);

        // then
        assertAll(
                () -> assertThat(categoryRepository.findById(categoryIdResponse.categoryId())).isEmpty(),
                () -> assertThat(categoryMemberRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("HOST가 카테고리를 삭제하면 속한 스타카토, 댓글, 함께하는 사람, 초대 요청도 함께 삭제된다.")
    @Test
    void deleteCategoryWithStaccato() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        Member guest2 = MemberFixtures.defaultMember().withNickname("guest2").buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(host).buildAndSave(commentRepository);
        categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest2));

        // when
        categoryService.deleteCategory(category.getId(), host);

        // then
        assertAll(
                () -> assertThat(categoryRepository.findById(category.getId())).isEmpty(),
                () -> assertThat(categoryMemberRepository.findAll()).isEmpty(),
                () -> assertThat(staccatoRepository.findAll()).isEmpty(),
                () -> assertThat(commentRepository.findAll()).isEmpty(),
                () -> assertThat(categoryInvitationRepository.findAll()).isEmpty()
        );
    }

    @DisplayName("본인 것이 아닌 카테고리 상세를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteCategoryIfNotOwner() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().withNickname("otherMem").buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.defaultCategoryCreateRequest().build(), member);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("카테고리 생성 시 생성된 CategoryMember의 역할은 HOST이다.")
    @Test
    void createCategorySetsMemberRoleAsHost() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.defaultCategoryCreateRequest()
                .build();

        // when
        categoryService.createCategory(categoryCreateRequest, member);

        // then
        CategoryMember categoryMember = categoryMemberRepository.findAllByMemberId(member.getId()).get(0);

        assertThat(categoryMember.getRole()).isEqualTo(Role.HOST);
    }

    @DisplayName("GUEST가 카테고리 수정을 시도한 경우, 예외를 발생한다.")
    @Test
    void failUpdateCategoryIfGuestMemberTried() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withGuests(List.of(member))
                .buildAndSave(categoryRepository);
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.defaultCategoryUpdateRequest()
                .build();

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryUpdateRequest, category.getId(), member))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("GUEST가 카테고리 삭제를 시도한 경우, 예외를 발생한다.")
    @Test
    void failDeleteCategoryIfGuestMemberTried() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withGuests(List.of(member))
                .buildAndSave(categoryRepository);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(category.getId(), member))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("GUEST가 카테고리 색상 변경을 시도한 경우, 예외를 발생한다.")
    @Test
    void failUpdateCategoryColorIfGuestMemberTried() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withGuests(List.of(member))
                .buildAndSave(categoryRepository);
        CategoryColorRequest categoryColorRequest = new CategoryColorRequest(Color.BLUE.getName());

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategoryColor(category.getId(), categoryColorRequest, member))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("카테고리에 포함된 멤버의 역할을 반환한다.")
    @Test
    void getRoleOfMemberReturnsRole() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when
        Role hostRole = category.getRoleOfMember(host);
        Role guestRole = category.getRoleOfMember(guest);

        // then
        assertAll(
                () -> assertThat(hostRole).isEqualTo(Role.HOST),
                () -> assertThat(guestRole).isEqualTo(Role.GUEST)
        );
    }
}
