package com.staccato.staccato.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Color;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
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
import com.staccato.staccato.service.dto.request.StaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponses;

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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
                .withCategoryId(0L).build();

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccato(staccatoRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 카테고리를 찾을 수 없어요.");
    }

    @DisplayName("스타카토 목록 조회에 성공한다.")
    @Test
    void readAllStaccato() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withColor(Color.BLUE)
                .buildAndSaveWithMember(member, categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .withTitle("title2")
                .withColor(Color.PINK)
                .buildAndSaveWithMember(member, categoryRepository);
        StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        StaccatoFixtures.defaultStaccato()
                .withCategory(category2).buildAndSave(staccatoRepository);

        // when
        StaccatoLocationResponses responses = staccatoService.readAllStaccato(member);

        // then
        assertAll(
                () -> assertThat(responses.staccatoLocationResponses()).hasSize(3),
                () -> assertThat(responses.staccatoLocationResponses().stream()
                        .map(StaccatoLocationResponse::staccatoColor)
                        .toList())
                        .containsExactlyInAnyOrder(Color.BLUE.getName(), Color.BLUE.getName(), Color.PINK.getName())
                );
    }

    @DisplayName("스타카토 조회에 성공한다.")
    @Test
    void readStaccatoById() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        StaccatoDetailResponse actual = staccatoService.readStaccatoById(staccato.getId(), member);

        // then
        assertThat(actual).isEqualTo(new StaccatoDetailResponse(staccato));
    }

    @DisplayName("본인 것이 아닌 스타카토를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadStaccatoByIdIfNotOwner() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when & then
        assertThatThrownBy(() -> staccatoService.readStaccatoById(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 스타카토를 조회하면 예외가 발생한다.")
    @Test
    void failReadStaccatoById() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        // when & then
        assertThatThrownBy(() -> staccatoService.readStaccatoById(1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("스타카토 수정에 성공한다.")
    @Test
    void updateStaccatoById() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category1 = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Category category2 = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category1)
                .buildAndSaveWithStaccatoImages(List.of("https://example.com/staccatoImage1.jpg", "https://example.com/staccatoImage2.jpg"), staccatoRepository);

        // when
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
                .withStaccatoTitle("newStaccatoTitle")
                .withCategoryId(category2.getId())
                .withStaccatoImageUrls(List.of("https://example.com/staccatoImage2.jpg", "https://example.com/staccatoImage3.jpg"))
                .build();
        staccatoService.updateStaccatoById(staccato.getId(), staccatoRequest, member);

        // then
        Staccato foundedStaccato = staccatoRepository.findById(staccato.getId()).get();
        List<StaccatoImage> images = staccatoImageRepository.findAll();
        assertAll(
                () -> assertThat(foundedStaccato.getTitle()).isEqualTo("newStaccatoTitle"),
                () -> assertThat(foundedStaccato.getCategory().getId()).isEqualTo(category2.getId()),
                () -> assertThat(images.size()).isEqualTo(2),
                () -> assertThat(images.get(0).getImageUrl()).isEqualTo("https://example.com/staccatoImage2.jpg"),
                () -> assertThat(images.get(1).getImageUrl()).isEqualTo("https://example.com/staccatoImage3.jpg")
        );
    }

    @DisplayName("본인 것이 아닌 스타카토를 수정하려고 하면 예외가 발생한다.")
    @Test
    void failToUpdateStaccatoOfOther() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Category otherCategory = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(otherMember, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        StaccatoRequest staccatoRequest = StaccatoRequestFixtures.defaultStaccatoRequest()
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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .buildAndSaveWithStaccatoImages(List.of("https://example.com/staccatoImage1.jpg", "https://example.com/staccatoImage2.jpg"), staccatoRepository);
        Comment comment = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(member).buildAndSave(commentRepository);

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
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when & then
        assertThatThrownBy(() -> staccatoService.deleteStaccatoById(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("스타카토의 기분을 선택할 수 있다.")
    @Test
    void updateStaccatoFeelingById() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
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
}
