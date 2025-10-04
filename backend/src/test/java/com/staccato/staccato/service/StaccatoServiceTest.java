package com.staccato.staccato.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.exception.ConflictException;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.fixture.staccato.StaccatoRequestFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.domain.Feeling;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.domain.StaccatoImage;
import com.staccato.staccato.repository.StaccatoImageRepository;
import com.staccato.staccato.repository.StaccatoRepository;
import com.staccato.staccato.service.dto.request.FeelingRequest;
import com.staccato.staccato.service.dto.request.StaccatoLocationRangeRequest;
import com.staccato.staccato.service.dto.request.StaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StaccatoServiceTest extends ServiceSliceTest {
    @Autowired
    private StaccatoService staccatoService;
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private StaccatoImageRepository staccatoImageRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("사진 없이도 스타카토를 생성할 수 있다.")
    @Test
    void createStaccato() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withCategoryId(category.getId()).build();

        // when
        long staccatoId = staccatoService.createStaccato(staccatoRequest, member).staccatoId();

        // then
        assertThat(staccatoRepository.findById(staccatoId)).isNotEmpty();
    }

    @DisplayName("스타카토를 생성하면 Staccato과 StaccatoImage들이 함께 저장되고 id를 반환한다.")
    @Test
    void createStaccatoWithStaccatoImages() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withCategoryId(category.getId())
                .withStaccatoImageUrls(List.of("https://example.com/staccatoImage.jpg")).build();

        // when
        long staccatoId = staccatoService.createStaccato(staccatoRequest, member).staccatoId();

        // then
        assertAll(
                () -> assertThat(staccatoRepository.findById(staccatoId)).isNotEmpty(),
                () -> assertThat(staccatoImageRepository.findAll().size()).isEqualTo(1),
                () -> assertThat(staccatoImageRepository.findAll().get(0).getStaccato().getId()).isEqualTo(staccatoId)
        );
    }

    @DisplayName("본인 것이 아닌 카테고리에 스타카토를 생성하려고 하면 예외가 발생한다.")
    @Test
    void cannotCreateStaccatoIfNotOwner() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.ofDefault().withNickname("otherMem").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withCategoryId(category.getId()).build();

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccato(staccatoRequest, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 카테고리에 스타카토 생성을 시도하면 예외가 발생한다.")
    @Test
    void failCreateStaccato() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withCategoryId(0L).build();

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccato(staccatoRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 카테고리를 찾을 수 없어요.");
    }

    @DisplayName("위경도 조건이 없으므로, 사용자의 모든 스타카토 목록을 조회한다.")
    @Test
    void readAllStaccato() {
        // given
        Member member = MemberFixtures.ofDefault().withCode("me").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withColor(Color.BLUE)
                .withHost(member)
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.ofDefault()
                .withTitle("title2")
                .withColor(Color.PINK)
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.ofDefault(category2).buildAndSave(staccatoRepository);
        Staccato staccato3 = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);

        // when
        StaccatoLocationResponsesV2 responses = staccatoService.readAllStaccato(member, StaccatoLocationRangeRequest.empty());

        // then
        assertAll(
                () -> assertThat(responses.staccatoLocationResponses()).hasSize(3),
                () -> assertThat(responses.staccatoLocationResponses())
                        .containsExactlyInAnyOrder(
                                new StaccatoLocationResponseV2(staccato),
                                new StaccatoLocationResponseV2(staccato2),
                                new StaccatoLocationResponseV2(staccato3)
                        )
        );
    }

    @DisplayName("스타카토 조회에 성공한다.")
    @Test
    void readStaccatoById() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);

        // when
        StaccatoDetailResponseV2 actual = staccatoService.readStaccatoById(staccato.getId(), member);

        // then
        assertThat(actual).isEqualTo(new StaccatoDetailResponseV2(staccato));
    }

    @DisplayName("본인 것이 아닌 스타카토를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadStaccatoByIdIfNotOwner() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.ofDefault().withNickname("otherMem").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);

        // when & then
        assertThatThrownBy(() -> staccatoService.readStaccatoById(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 스타카토를 조회하면 예외가 발생한다.")
    @Test
    void failReadStaccatoById() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);

        // when & then
        assertThatThrownBy(() -> staccatoService.readStaccatoById(1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("스타카토 수정에 성공한다.")
    @Test
    void updateStaccatoById() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Category category2 = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category1)
                .withStaccatoImages(List.of("https://example.com/staccatoImage1.jpg", "https://example.com/staccatoImage2.jpg"))
                .buildAndSave(staccatoRepository);

        // when
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withStaccatoTitle("newStaccatoTitle")
                .withCategoryId(category2.getId())
                .withStaccatoImageUrls(List.of("https://example.com/staccatoImage2.jpg", "https://example.com/staccatoImage3.jpg"))
                .build();
        staccatoService.updateStaccatoById(staccato.getId(), staccatoRequest, member);

        // then
        Staccato foundedStaccato = staccatoRepository.findById(staccato.getId()).get();
        List<StaccatoImage> images = staccatoImageRepository.findAll();
        assertAll(
                () -> assertThat(foundedStaccato.getTitle().getTitle()).isEqualTo(staccatoRequest.staccatoTitle()),
                () -> assertThat(foundedStaccato.getCategory().getId()).isEqualTo(category2.getId()),
                () -> assertThat(images.size()).isEqualTo(2),
                () -> assertThat(images.get(0).getImageUrl()).isEqualTo("https://example.com/staccatoImage2.jpg"),
                () -> assertThat(images.get(1).getImageUrl()).isEqualTo("https://example.com/staccatoImage3.jpg")
        );
    }

    @Nested
    @DisplayName("스타카토의 카테고리 변경 시 예외 처리")
    class UpdateCategoryChangeTests {

        private Member member1;
        private Member member2;

        private Category privateCategory1;
        private Category privateCategory2;
        private Category sharedCategory1;
        private Category sharedCategory2;

        private Staccato staccatoInPrivate1;
        private Staccato staccatoInShared1;

        @BeforeEach
        void setUp() {
            member1 = MemberFixtures.ofDefault().withNickname("member1").buildAndSave(memberRepository);
            member2 = MemberFixtures.ofDefault().withNickname("member2").buildAndSave(memberRepository);
            privateCategory1 = CategoryFixtures.ofDefault()
                    .withHost(member1)
                    .buildAndSave(categoryRepository);
            privateCategory2 = CategoryFixtures.ofDefault()
                    .withHost(member1)
                    .buildAndSave(categoryRepository);
            sharedCategory1 = CategoryFixtures.ofDefault()
                    .withHost(member1)
                    .withGuests(List.of(member2))
                    .buildAndSave(categoryRepository);
            sharedCategory2 = CategoryFixtures.ofDefault()
                    .withHost(member1)
                    .withGuests(List.of(member2))
                    .buildAndSave(categoryRepository);
            staccatoInPrivate1 = StaccatoFixtures.ofDefault(privateCategory1)
                    .buildAndSave(staccatoRepository);
            staccatoInShared1 = StaccatoFixtures.ofDefault(sharedCategory1)
                    .buildAndSave(staccatoRepository);
        }

        @DisplayName("카테고리 ID를 변경하지 않는다면 스타카토 수정에 제약은 없다.")
        @Test
        void updateDoesNotThrow_whenCategoryIsUnchanged() {
            Long staccatoId = staccatoInShared1.getId();
            Long sameCategoryId = sharedCategory1.getId();

            StaccatoRequest request = StaccatoRequestFixtures.ofDefault()
                    .withCategoryId(sameCategoryId)
                    .build();

            assertDoesNotThrow(() -> staccatoService.updateStaccatoById(staccatoId, request, member1));
        }

        @DisplayName("개인 -> 개인 카테고리 이동은 가능하다.")
        @Test
        void updateAllows_privateToPrivate() {
            Long staccatoId = staccatoInPrivate1.getId();

            StaccatoRequest request = StaccatoRequestFixtures.ofDefault()
                    .withCategoryId(privateCategory2.getId())
                    .build();

            assertDoesNotThrow(() -> staccatoService.updateStaccatoById(staccatoId, request, member1));
        }

        @DisplayName("개인 → 공동 카테고리 이동은 예외가 발생한다.")
        @Test
        void updateThrows_privateToShared() {
            Long staccatoId = staccatoInPrivate1.getId();

            StaccatoRequest request = StaccatoRequestFixtures.ofDefault()
                    .withCategoryId(sharedCategory1.getId())
                    .build();

            assertThatThrownBy(() -> staccatoService.updateStaccatoById(staccatoId, request, member1))
                    .isInstanceOf(StaccatoException.class)
                    .hasMessage("개인 카테고리 간에만 스타카토를 옮길 수 있어요.");
        }

        @DisplayName("공동 → 개인 카테고리 이동은 예외가 발생한다.")
        @Test
        void updateThrows_sharedToPrivate() {
            Long staccatoId = staccatoInShared1.getId();

            StaccatoRequest request = StaccatoRequestFixtures.ofDefault()
                    .withCategoryId(privateCategory1.getId())
                    .build();

            assertThatThrownBy(() -> staccatoService.updateStaccatoById(staccatoId, request, member1))
                    .isInstanceOf(StaccatoException.class)
                    .hasMessage("개인 카테고리 간에만 스타카토를 옮길 수 있어요.");
        }

        @DisplayName("공동 → 공동 카테고리 간 이동은 예외가 발생한다.")
        @Test
        void updateThrows_sharedToShared() {
            Long staccatoId = staccatoInShared1.getId();

            StaccatoRequest request = StaccatoRequestFixtures.ofDefault()
                    .withCategoryId(sharedCategory2.getId())
                    .build();

            assertThatThrownBy(() -> staccatoService.updateStaccatoById(staccatoId, request, member1))
                    .isInstanceOf(StaccatoException.class)
                    .hasMessage("개인 카테고리 간에만 스타카토를 옮길 수 있어요.");
        }
    }

    @DisplayName("본인 것이 아닌 스타카토를 수정하려고 하면 예외가 발생한다.")
    @Test
    void failToUpdateStaccatoOfOther() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.ofDefault().withNickname("otherMem").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withCategoryId(category.getId()).build();

        // when & then
        assertThatThrownBy(() -> staccatoService.updateStaccatoById(staccato.getId(), staccatoRequest, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본인 것이 아닌 카테고리에 속하도록 스타카토를 수정하려고 하면 예외가 발생한다.")
    @Test
    void failToUpdateStaccatoToOtherCategory() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.ofDefault().withNickname("otherMem").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Category otherCategory = CategoryFixtures.ofDefault()
                .withHost(otherMember).buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withCategoryId(otherCategory.getId()).build();

        // when & then
        assertThatThrownBy(() -> staccatoService.updateStaccatoById(staccato.getId(), staccatoRequest, member))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 스타카토를 수정하면 예외가 발생한다.")
    @Test
    void failToUpdateNotExistStaccato() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.ofDefault()
                .withCategoryId(category.getId()).build();

        // when & then
        assertThatThrownBy(() -> staccatoService.updateStaccatoById(1L, staccatoRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("Staccato를 삭제하면 이에 포함된 StaccatoImage와 StaccatoLog도 모두 삭제된다.")
    @Test
    void deleteStaccatoById() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category)
                .withStaccatoImages(List.of("https://example.com/staccatoImage1.jpg", "https://example.com/staccatoImage2.jpg"))
                .buildAndSave(staccatoRepository);
        Comment comment = CommentFixtures.ofDefault(staccato, member).buildAndSave(commentRepository);

        // when
        staccatoService.deleteStaccatoById(staccato.getId(), member);

        // then
        assertAll(
                () -> assertThat(staccatoRepository.findById(staccato.getId())).isEmpty(),
                () -> assertThat(commentRepository.findById(comment.getId())).isEmpty(),
                () -> assertThat(staccatoImageRepository.findById(0L)).isEmpty(),
                () -> assertThat(staccatoImageRepository.findById(1L)).isEmpty()
        );
    }

    @DisplayName("본인 것이 아닌 스타카토를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteStaccatoByIdIfNotOwner() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.ofDefault().withNickname("otherMem").buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);

        // when & then
        assertThatThrownBy(() -> staccatoService.deleteStaccatoById(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("스타카토의 기분을 선택할 수 있다.")
    @Test
    void updateStaccatoFeelingById() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.ofDefault(category).buildAndSave(staccatoRepository);
        FeelingRequest feelingRequest = new FeelingRequest("happy");

        // when
        staccatoService.updateStaccatoFeelingById(staccato.getId(), member, feelingRequest);

        // then
        assertAll(
                () -> assertThat(staccatoRepository.findById(staccato.getId())).isNotEmpty(),
                () -> assertThat(staccatoRepository.findById(staccato.getId()).get()
                        .getFeeling()).isEqualTo(Feeling.HAPPY)
        );
    }

    @Nested
    @Disabled
    @DisplayName("스타카토 동시성 테스트")
    class StaccatoConcurrency {
        private Member member;
        private Category category;
        private Staccato staccato;

        @BeforeEach
        void setUp() {
            member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
            category = CategoryFixtures.ofDefault()
                    .withHost(member)
                    .buildAndSave(categoryRepository);
            staccato = StaccatoFixtures.ofDefault(category)
                    .withTitle("origin")
                    .withFeeling(Feeling.ANGRY)
                    .buildAndSave(staccatoRepository);
        }

        @DisplayName("동일한 Staccato를 순차적으로 수정하면 예외가 발생하지 않는다.")
        @Test
        void sequentialUpdatesNoConflict() {
            // given
            StaccatoRequest request1 = StaccatoRequestFixtures.ofDefault()
                    .withStaccatoTitle("first")
                    .withCategoryId(category.getId())
                    .build();
            StaccatoRequest request2 = StaccatoRequestFixtures.ofDefault()
                    .withStaccatoTitle("second")
                    .withCategoryId(category.getId())
                    .build();

            // when & then
            assertAll(
                    () -> assertThatNoException().isThrownBy(() ->
                            staccatoService.updateStaccatoById(staccato.getId(), request1, member)),
                    () -> assertThatNoException().isThrownBy(() ->
                            staccatoService.updateStaccatoById(staccato.getId(), request2, member)),
                    () -> assertThat(staccatoRepository.findById(staccato.getId())
                            .get().getTitle().getTitle())
                            .isEqualTo("second")
            );
        }

        @DisplayName("동일한 Staccato를 동시에 수정하면 예외가 발생한다.")
        @Test
        void failOnConcurrentUpdate() {
            // given
            StaccatoRequest request1 = StaccatoRequestFixtures.ofDefault()
                    .withStaccatoTitle("first")
                    .withCategoryId(category.getId())
                    .build();

            StaccatoRequest request2 = StaccatoRequestFixtures.ofDefault()
                    .withStaccatoTitle("second")
                    .withCategoryId(category.getId())
                    .build();

            // when
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            Future<?> future = executorService.submit(() -> staccatoService.updateStaccatoById(staccato.getId(), request1, member));
            Future<?> future2 = executorService.submit(() -> staccatoService.updateStaccatoById(staccato.getId(), request2, member));

            // then
            assertAll(
                    () -> assertThatThrownBy(() -> {
                        future.get();
                        future2.get();
                    }).hasCauseInstanceOf(ConflictException.class)
                            .hasMessageContaining("누군가 이 스타카토를 먼저 수정했어요. 최신 상태를 불러온 뒤 다시 시도해주세요."),
                    () -> assertThat(staccatoRepository.findById(staccato.getId()).get().getTitle().getTitle())
                            .isIn("first", "second")
            );
        }

        @DisplayName("동시에 Staccato의 기분을 수정하면 예외가 발생한다.")
        @Test
        void failOnConcurrentFeelingUpdate() {
            // given
            FeelingRequest request1 = new FeelingRequest(Feeling.HAPPY.getValue());
            FeelingRequest request2 = new FeelingRequest(Feeling.SAD.getValue());

            // when
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            Future<?> future = executorService.submit(() -> staccatoService.updateStaccatoFeelingById(staccato.getId(), member, request1));
            Future<?> future2 = executorService.submit(() -> staccatoService.updateStaccatoFeelingById(staccato.getId(), member, request2));

            // then
            assertAll(
                    () -> assertThatThrownBy(() -> {
                        future.get();
                        future2.get();
                    }).hasCauseInstanceOf(ConflictException.class)
                            .hasMessageContaining("누군가 기분을 먼저 수정했어요. 최신 상태를 불러온 뒤 다시 시도해주세요."),
                    () -> assertThat(staccatoRepository.findById(staccato.getId()).get().getFeeling())
                            .isIn(Feeling.SAD, Feeling.HAPPY)
            );
        }
    }
}
