package com.staccato.category.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import com.staccato.category.service.dto.response.CategoryDetailResponseV4;
import com.staccato.category.service.dto.response.CategoryIdResponse;
import com.staccato.category.service.dto.response.CategoryNameResponse;
import com.staccato.category.service.dto.response.CategoryNameResponses;
import com.staccato.category.service.dto.response.CategoryResponsesV3;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponse;
import com.staccato.category.service.dto.response.CategoryStaccatoLocationResponses;
import com.staccato.category.service.dto.response.CategoryStaccatoResponses;
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

    static Stream<Arguments> updateCategoryProvider() {
        return Stream.of(
                Arguments.of(CategoryUpdateRequestFixtures.ofDefault()
                        .withCategoryThumbnailUrl(null).build(), null),
                Arguments.of(CategoryUpdateRequestFixtures.ofDefault()
                        .withCategoryThumbnailUrl("https://example.com/categoryThumbnailUrl.jpg")
                        .build(), "https://example.com/categoryThumbnailUrl.jpg"));
    }

    @DisplayName("카테고리 정보를 기반으로, 카테고리를 생성하고 작성자를 저장한다.")
    @Test
    void createCategory() {
        // given
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.ofDefault().build();
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);

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
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.ofDefault().build();
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);

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
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.ofDefault()
                .build();
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
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
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.ofDefault()
                .build();
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.ofDefault().withNickname("otherMem").buildAndSave(memberRepository);
        categoryService.createCategory(categoryCreateRequest, otherMember);

        // when & then
        assertThatNoException().isThrownBy(() -> categoryService.createCategory(categoryCreateRequest, member));
    }

    @DisplayName("CategoryMember 목록을 최근 수정 순으로 조회된다.")
    @Test
    void readAllCategoriesOrderByUpdatedAtDesc() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.ofDefault()
                        .withCategoryTitle("first").build(), member);
        categoryService.createCategory(
                CategoryCreateRequestFixtures.ofDefault()
                        .withCategoryTitle("second").build(), member);
        staccatoService.createStaccato(StaccatoRequestFixtures.ofDefault()
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

    @Nested
    @DisplayName("특정 카테고리 조회 시")
    class ReadCategoryTest {
        Member host;
        Member guest;
        Member other;
        Category category;

        @BeforeEach
        void setUp() {
            host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
            guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
            other = MemberFixtures.ofDefault().withNickname("other").buildAndSave(memberRepository);

            category = CategoryFixtures.ofDefault()
                    .withTerm(LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 1, 4))
                    .withHost(host)
                    .withGuests(List.of(guest))
                    .buildAndSave(categoryRepository);
        }

        @Nested
        @DisplayName("카테고리 조회(readCategoryById)일 경우")
        class ReadCategory {
            @DisplayName("HOST는 정상적으로 조회할 수 있다.")
            @Test
            void successForHost() {
                // when
                CategoryDetailResponseV4 response = categoryService.readCategoryById(category.getId(), host);

                // then
                assertAll(
                        () -> assertThat(response.categoryId()).isEqualTo(category.getId()),
                        () -> assertThat(response.members()).hasSize(2)
                );
            }

            @DisplayName("GUEST도 정상적으로 조회할 수 있다.")
            @Test
            void successForGuest() {
                CategoryDetailResponseV4 response = categoryService.readCategoryById(category.getId(), guest);
                assertThat(response.categoryId()).isEqualTo(category.getId());
                assertThat(response.members()).hasSize(2);
            }

            @DisplayName("기간이 없는 카테고리도 정상 조회된다.")
            @Test
            void successWhenTermIsNull() {
                // given
                category = CategoryFixtures.ofDefault()
                        .withHost(host)
                        .buildAndSave(categoryRepository);

                // when
                CategoryDetailResponseV4 response = categoryService.readCategoryById(category.getId(), host);

                // then
                assertAll(
                        () -> assertThat(response.categoryId()).isEqualTo(category.getId()),
                        () -> assertThat(response.startAt()).isNull(),
                        () -> assertThat(response.endAt()).isNull()
                );
            }

            @DisplayName("카테고리에 속하지 않은 사용자는 조회할 수 없다.")
            @Test
            void failIfNotOwner() {
                assertThatThrownBy(() -> categoryService.readCategoryById(category.getId(), other))
                        .isInstanceOf(ForbiddenException.class)
                        .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
            }

            @DisplayName("존재하지 않는 카테고리일 경우 예외가 발생한다.")
            @Test
            void failIfNotExist() {
                assertThatThrownBy(() -> categoryService.readCategoryById(0L, host))
                        .isInstanceOf(StaccatoException.class)
                        .hasMessage("요청하신 카테고리를 찾을 수 없어요.");
            }
        }

        @Nested
        @DisplayName("스타카토를 포함한 카테고리 조회(readCategoryWithStaccatosById)일 경우")
        class ReadCategoryWithStaccatos {
            private Staccato staccato;
            private Staccato staccato2;

            @BeforeEach
            void setUpStaccatos() {
                staccato = StaccatoFixtures.ofDefault(category)
                        .withVisitedAt(LocalDateTime.of(2024, 1, 3, 0, 0, 0))
                        .build();
                staccato2 = StaccatoFixtures.ofDefault(category)
                        .withVisitedAt(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                        .build();
                staccatoRepository.saveAll(List.of(staccato, staccato2));
            }

            @DisplayName("HOST는 staccato 목록을 함께 조회한다.")
            @Test
            void successForHost() {
                // when
                CategoryDetailResponseV3 response = categoryService.readCategoryWithStaccatosById(category.getId(), host);

                // then
                assertAll(
                        () -> assertThat(response.categoryId()).isEqualTo(category.getId()),
                        () -> assertThat(response.staccatos()).hasSize(2)
                );
            }

            @DisplayName("GUEST도 staccato 목록을 함께 조회한다.")
            @Test
            void successForGuest() {
                // when
                CategoryDetailResponseV3 response = categoryService.readCategoryWithStaccatosById(category.getId(), guest);

                // then
                assertAll(
                        () -> assertThat(response.categoryId()).isEqualTo(category.getId()),
                        () -> assertThat(response.staccatos()).hasSize(2)
                );
            }

            @DisplayName("스타카토는 방문 날짜 기준 최신순으로 정렬된다.")
            @Test
            void successOrderByVisitedAtDesc() {
                // when
                CategoryDetailResponseV3 response = categoryService.readCategoryWithStaccatosById(category.getId(), host);

                // then
                assertAll(
                        () -> assertThat(response.categoryId()).isEqualTo(response.categoryId()),
                        () -> assertThat(response.staccatos()).hasSize(2),
                        () -> assertThat(response.staccatos()).containsExactly(
                                new StaccatoResponse(staccato),
                                new StaccatoResponse(staccato2)
                        )
                );
            }

            @DisplayName("기간이 없는 카테고리도 정상 조회된다.")
            @Test
            void successWhenTermIsNull() {
                // given
                category = CategoryFixtures.ofDefault()
                        .withHost(host)
                        .buildAndSave(categoryRepository);

                // when
                CategoryDetailResponseV3 response = categoryService.readCategoryWithStaccatosById(category.getId(), host);

                // then
                assertAll(
                        () -> assertThat(response.categoryId()).isEqualTo(category.getId()),
                        () -> assertThat(response.startAt()).isNull(),
                        () -> assertThat(response.endAt()).isNull()
                );
            }

            @DisplayName("카테고리에 속하지 않은 사용자는 조회할 수 없다.")
            @Test
            void failIfNotOwner() {
                assertThatThrownBy(() -> categoryService.readCategoryById(category.getId(), other))
                        .isInstanceOf(ForbiddenException.class)
                        .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
            }

            @DisplayName("존재하지 않는 카테고리일 경우 예외가 발생한다.")
            @Test
            void failIfNotExist() {
                assertThatThrownBy(() -> categoryService.readCategoryWithStaccatosById(999L, host))
                        .isInstanceOf(StaccatoException.class)
                        .hasMessage("요청하신 카테고리를 찾을 수 없어요.");
            }
        }
    }

    @DisplayName("위경도 조건이 없으면 카테고리에 속한 모든 스타카토 위치 목록을 조회한다.")
    @Test
    void readAllStaccatoLocations() {
        // given
        Member member = MemberFixtures.ofDefault().withCode("me").buildAndSave(memberRepository);
        Member member2 = MemberFixtures.ofDefault().withNickname("other").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withColor(Color.BLUE)
                .withHost(member)
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.ofDefault()
                .withTitle("title2")
                .withColor(Color.PINK)
                .withHost(member2)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
        Staccato otherStaccato = StaccatoFixtures.ofDefault(category2).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);

        // when
        CategoryStaccatoLocationResponses responses = categoryService.readStaccatoLocationsByCategory(
                member, category.getId(), new CategoryStaccatoLocationRangeRequest(null, null, null, null));

        // then
        assertAll(
                () -> assertThat(responses.staccatos()).hasSize(2),
                () -> assertThat(responses.staccatos())
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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);

        StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
        StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);

        CategoryReadRequest request = new CategoryReadRequest(null, null);

        // when
        CategoryResponsesV3 categoryResponses = categoryService.readAllCategories(member, request);

        // then
        assertAll(
                () -> assertThat(categoryResponses.categories()).hasSize(1),
                () -> assertThat(categoryResponses.categories().get(0).staccatoCount()).isEqualTo(2L)
        );
    }

    @DisplayName("개인카테고리여부(isPrivate)가 false이면 해당 멤버의 전체 카테고리(개인/공동) 목록을 수정 순으로 조회한다.")
    @Test
    void readAllCategoriesByMemberAndDateAndPrivateFlagWhenPrivateFlagIsFalse() {
        // given
        Member host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        Category privateCategory = CategoryFixtures.ofDefault()
                .withHost(host)
                .buildAndSave(categoryRepository);
        Category publicCategory = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when
        boolean isPrivate = false;
        CategoryNameResponses categoryNameResponses = categoryService.readAllCategoriesByMemberAndDateAndPrivateFlag(host, LocalDate.of(2024, 6, 1), isPrivate);

        // then
        List<Long> resultCategoryIds = categoryNameResponses.categories().stream()
                .map(CategoryNameResponse::categoryId)
                .toList();

        assertThat(resultCategoryIds).hasSize(2)
                .containsExactly(publicCategory.getId(), privateCategory.getId());
    }

    @DisplayName("개인카테고리여부(isPrivate)가 true이면 해당 멤버의 개인 카테고리 목록을 수정 순으로 조회한다.")
    @Test
    void readAllCategoriesByMemberAndDateAndPrivateFlagWhenPrivateFlagIsTrue() {
        // given
        Member host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        Category privateCategory = CategoryFixtures.ofDefault()
                .withHost(host)
                .buildAndSave(categoryRepository);
        Category publicCategory = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when
        boolean isPrivate = true;
        CategoryNameResponses categoryNameResponses = categoryService.readAllCategoriesByMemberAndDateAndPrivateFlag(host, LocalDate.of(2024, 6, 1), isPrivate);

        // then
        List<Long> resultCategoryIds = categoryNameResponses.categories().stream()
                .map(CategoryNameResponse::categoryId)
                .toList();

        assertThat(resultCategoryIds).hasSize(1)
                .containsExactly(privateCategory.getId())
                .doesNotContain(publicCategory.getId());
    }

    @Nested
    @DisplayName("카테고리 내 스타카토 목록 조회 시")
    class readStaccatosInCategory {
        private Member member;
        private Category category;
        private List<Staccato> staccatos;

        @BeforeEach
        void setup() {
            member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
            category = CategoryFixtures.ofDefault()
                    .withHost(member)
                    .buildAndSave(categoryRepository);
            staccatos = new ArrayList<>();
            for (int count = 1; count <= 10; count++) {
                staccatos.add(StaccatoFixtures.ofDefault(category)
                        .withCreator(member)
                        .withTitle("staccato " + count)
                        .withVisitedAt(LocalDateTime.of(2024, 1, count, 0, 0, 0))
                        .buildAndSave(staccatoRepository)
                );
            }

            staccatos.sort(
                    Comparator.comparing(Staccato::getVisitedAt).reversed()
                            .thenComparing(Comparator.comparing(Staccato::getCreatedAt).reversed())
            );
        }

        @DisplayName("주어진 커서가 없다면, 첫 페이지를 조회한다.")
        @Test
        void readStaccatosForFirstPage() {
            // given
            int limit = 1;
            Staccato expectedStaccato = staccatos.get(0);
            StaccatoCursor nextCursor = new StaccatoCursor(expectedStaccato);

            // when
            CategoryStaccatoResponses categoryStaccatoResponses = categoryService.readStaccatosByCategory(member, category.getId(), null, limit);

            // then
            assertAll(
                    () -> assertThat(categoryStaccatoResponses.staccatos()).hasSize(limit),
                    () -> assertThat(categoryStaccatoResponses.staccatos().get(0).staccatoId())
                            .isEqualTo(expectedStaccato.getId()),
                    () -> assertThat(StaccatoCursor.fromEncoded(categoryStaccatoResponses.nextCursor())).isEqualTo(nextCursor)
            );
        }

        @DisplayName("주어진 커서 다음 데이터부터 조회한다.")
        @Test
        void readStaccatosAfterCursor() {
            // given
            Staccato cursorStaccato = staccatos.get(0);
            StaccatoCursor cursor = new StaccatoCursor(cursorStaccato);
            Staccato expectedStaccato = staccatos.get(1);

            // when
            CategoryStaccatoResponses response = categoryService.readStaccatosByCategory(member, category.getId(), cursor.encode(), 1);

            // then
            assertAll(
                    () -> assertThat(response.staccatos()).hasSize(1),
                    () -> assertThat(response.staccatos().get(0).staccatoId()).isEqualTo(expectedStaccato.getId())
            );
        }

        @DisplayName("스타카토 목록과 함께 다음 페이지를 읽기 위해 마지막으로 읽었던 스타카토 기반의 nextCursor를 알려준다.")
        @Test
        void readStaccatosWithNextCursor() {
            // given
            Staccato cursorStaccato = staccatos.get(0);
            int lastReadIndex = 2;
            StaccatoCursor cursor = new StaccatoCursor(cursorStaccato);
            StaccatoCursor nextCursor = new StaccatoCursor(staccatos.get(lastReadIndex));

            // when
            CategoryStaccatoResponses response = categoryService.readStaccatosByCategory(member, category.getId(), cursor.encode(), 2);

            // then
            assertThat(StaccatoCursor.fromEncoded(response.nextCursor())).isEqualTo(nextCursor);
        }

        @DisplayName("더 이상 다음 페이지가 없다면 null을 반환한다.")
        @Test
        void readStaccatosWithNextCursorNull() {
            // given
            Staccato lastStaccato = staccatos.get(9);
            StaccatoCursor cursor = new StaccatoCursor(lastStaccato);

            // when
            CategoryStaccatoResponses response = categoryService.readStaccatosByCategory(member, category.getId(), cursor.encode(), 1);

            // then
            assertAll(
                    () -> assertThat(response.staccatos()).isEmpty(),
                    () -> assertThat(response.nextCursor()).isNull()

            );
        }

        @DisplayName("카테고리가 존재하지 않으면 예외를 반환한다.")
        @Test
        void failIfCategoryNotExist() {
            // given
            long invalidCategoryId = 0L;

            // when & then
            assertThatThrownBy(() ->
                    categoryService.readStaccatosByCategory(member, invalidCategoryId, null, 10)
            )
                    .isInstanceOf(StaccatoException.class)
                    .hasMessage("요청하신 카테고리를 찾을 수 없어요.");
        }
    }

    @DisplayName("카테고리 정보를 기반으로, 카테고리를 수정한다.")
    @MethodSource("updateCategoryProvider")
    @ParameterizedTest
    void updateCategory(CategoryUpdateRequest categoryUpdateRequest, String expected) {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.ofDefault().build(), member);

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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.ofDefault()
                        .withTerm(LocalDate.of(2024, 1, 1),
                                LocalDate.of(2024, 12, 31)).build(), member);

        // when
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.ofDefault()
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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.ofDefault()
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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.ofDefault().withNickname("otherMem").buildAndSave(memberRepository);
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.ofDefault()
                .build();
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.ofDefault().build(), member);

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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.ofDefault()
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
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.ofDefault()
                .withCategoryTitle("title").build();
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.ofDefault()
                .withCategoryTitle("title").build();
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(categoryCreateRequest, member);

        // when & then
        assertThatNoException().isThrownBy(() -> categoryService.updateCategory(categoryUpdateRequest, categoryIdResponse.categoryId(), member));
    }

    @DisplayName("이미 존재하는 이름으로 카테고리를 수정할 수 없다.")
    @Test
    void cannotUpdateCategoryByDuplicatedTitle() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);

        CategoryCreateRequest existingTitleRequest = CategoryCreateRequestFixtures.ofDefault()
                .withCategoryTitle("existingTitle").build();
        categoryService.createCategory(existingTitleRequest, member);

        CategoryCreateRequest editableCategoryRequest = CategoryCreateRequestFixtures.ofDefault()
                .withCategoryTitle("otherTitle").build();
        CategoryIdResponse editableCategoryId = categoryService.createCategory(editableCategoryRequest, member);

        // when & then
        CategoryUpdateRequest updateToDuplicateTitleRequest = CategoryUpdateRequestFixtures.ofDefault()
                .withCategoryTitle(existingTitleRequest.categoryTitle()).build();

        assertThatThrownBy(() -> categoryService.updateCategory(updateToDuplicateTitleRequest, editableCategoryId.categoryId(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("같은 이름을 가진 카테고리가 있어요. 다른 이름으로 설정해주세요.");
    }

    @DisplayName("카테고리 식별값을 통해 카테고리를 삭제한다.")
    @Test
    void deleteCategory() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.ofDefault().build(), member);

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
        Member host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        Member guest2 = MemberFixtures.ofDefault().withNickname("guest2").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
        CommentFixtures.ofDefault(staccato, host).buildAndSave(commentRepository);
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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.ofDefault().withNickname("otherMem").buildAndSave(memberRepository);
        CategoryIdResponse categoryIdResponse = categoryService.createCategory(
                CategoryCreateRequestFixtures.ofDefault().build(), member);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryIdResponse.categoryId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("카테고리 생성 시 생성된 CategoryMember의 역할은 HOST이다.")
    @Test
    void createCategorySetsMemberRoleAsHost() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequestFixtures.ofDefault()
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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withGuests(List.of(member))
                .buildAndSave(categoryRepository);
        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequestFixtures.ofDefault()
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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
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
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
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
        Member host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
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

    @DisplayName("공동카테고리의 GUEST는 카테고리를 나갈 수 있다.")
    @Test
    void deleteSelfFromCategory() {
        // given
        Member host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when
        categoryService.deleteSelfFromCategory(category.getId(), guest);

        // then
        boolean memberInCategoryFlag = categoryMemberRepository.existsByCategoryIdAndMemberId(category.getId(), guest.getId());

        assertThat(memberInCategoryFlag).isFalse();
    }

    @DisplayName("카테고리 ID에 해당하는 카테고리가 없으면 예외를 발생한다.")
    @Test
    void failDeleteSelfFromCategoryIfCategoryNotExist() {
        // given
        Member host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteSelfFromCategory(category.getId() + 1, guest))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 카테고리를 찾을 수 없어요.");
    }

    @DisplayName("사용자가 카테고리의 함께하는 사람에 속하지 않으면 공동 카테고리를 나갈 권한이 없다.")
    @Test
    void failDeleteSelfFromCategoryIfMemberNotInCategory() {
        // given
        Member host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        Member other = MemberFixtures.ofDefault().withNickname("other").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteSelfFromCategory(category.getId(), other))
                .isInstanceOf(ForbiddenException.class);
    }

    @DisplayName("사용자가 카테고리의 HOST이면 공동 카테고리를 나갈 수 없다.")
    @Test
    void failDeleteSelfFromCategoryIfMemberHost() {
        // given
        Member host = MemberFixtures.ofDefault().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.ofDefault().withNickname("guest").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteSelfFromCategory(category.getId(), host))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("방장은 카테고리를 나갈 수 없어요.");
    }
}
