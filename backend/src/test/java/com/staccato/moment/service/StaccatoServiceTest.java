package com.staccato.moment.service;

import com.staccato.category.domain.Category;
import com.staccato.moment.domain.Staccato;
import com.staccato.moment.service.dto.request.StaccatoRequest;
import com.staccato.moment.service.dto.response.StaccatoDetailResponse;
import com.staccato.moment.service.dto.response.StaccatoLocationResponses;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.comment.CommentFixture;
import com.staccato.fixture.category.CategoryFixture;
import com.staccato.fixture.moment.StaccatoFixture;
import com.staccato.fixture.moment.StaccatoRequestFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.moment.domain.Feeling;
import com.staccato.moment.domain.StaccatoImage;
import com.staccato.moment.repository.StaccatoImageRepository;
import com.staccato.moment.repository.StaccatoRepository;
import com.staccato.moment.service.dto.request.FeelingRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        Member member = saveMember();
        Category category = saveCategory(member);
        StaccatoRequest staccatoRequest = StaccatoRequestFixture.create(category.getId());

        // when
        long momentId = staccatoService.createStaccato(staccatoRequest, member).staccatoId();

        // then
        assertThat(staccatoRepository.findById(momentId)).isNotEmpty();
    }

    @DisplayName("스타카토를 생성하면 Moment과 MomentImage들이 함께 저장되고 id를 반환한다.")
    @Test
    void createMomentWithStaccatoImages() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        StaccatoRequest staccatoRequest = StaccatoRequestFixture.create(category.getId(), List.of("image.jpg"));

        // when
        long momentId = staccatoService.createStaccato(staccatoRequest, member).staccatoId();

        // then
        assertAll(
                () -> assertThat(staccatoRepository.findById(momentId)).isNotEmpty(),
                () -> assertThat(staccatoImageRepository.findAll().size()).isEqualTo(1),
                () -> assertThat(staccatoImageRepository.findAll().get(0).getStaccato().getId()).isEqualTo(momentId)
        );
    }

    @DisplayName("본인 것이 아닌 추억에 스타카토를 생성하려고 하면 예외가 발생한다.")
    @Test
    void cannotCreateStaccatoIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Category category = saveCategory(member);
        StaccatoRequest staccatoRequest = StaccatoRequestFixture.create(category.getId());

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccato(staccatoRequest, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 추억에 스타카토 생성을 시도하면 예외가 발생한다.")
    @Test
    void failCreateStaccato() {
        // given
        Member member = saveMember();
        StaccatoRequest staccatoRequest = StaccatoRequestFixture.create(0L);

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccato(staccatoRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 추억을 찾을 수 없어요.");
    }

    @DisplayName("스타카토 목록 조회에 성공한다.")
    @Test
    void readAllStaccato() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        saveStaccatoWithImages(category);
        saveStaccatoWithImages(category);

        // when
        StaccatoLocationResponses actual = staccatoService.readAllStaccato(member);

        // then
        assertThat(actual.staccatoLocationResponses()).hasSize(2);
    }

    @DisplayName("스타카토 조회에 성공한다.")
    @Test
    void readStaccatoById() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

        // when
        StaccatoDetailResponse actual = staccatoService.readStaccatoById(staccato.getId(), member);

        // then
        assertThat(actual).isEqualTo(new StaccatoDetailResponse(staccato));
    }

    @DisplayName("본인 것이 아닌 스타카토를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadStaccatoByIdIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

        // when & then
        assertThatThrownBy(() -> staccatoService.readStaccatoById(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 스타카토를 조회하면 예외가 발생한다.")
    @Test
    void failReadStaccatoById() {
        // given
        Member member = saveMember();

        // when & then
        assertThatThrownBy(() -> staccatoService.readStaccatoById(1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("스타카토 수정에 성공한다.")
    @Test
    void updateStaccatoById() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        Category category2 = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

        // when
        StaccatoRequest staccatoRequest = new StaccatoRequest("newStaccatoTitle", "placeName", "newAddress", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), category2.getId(), List.of("https://existExample.com.jpg", "https://newExample.com.jpg"));
        staccatoService.updateStaccatoById(staccato.getId(), staccatoRequest, member);

        // then
        Staccato foundedStaccato = staccatoRepository.findById(staccato.getId()).get();
        List<StaccatoImage> images = staccatoImageRepository.findAll();
        assertAll(
                () -> assertThat(foundedStaccato.getTitle()).isEqualTo("newStaccatoTitle"),
                () -> assertThat(foundedStaccato.getCategory().getId()).isEqualTo(category2.getId()),
                () -> assertThat(images.size()).isEqualTo(2),
                () -> assertThat(images.get(0).getImageUrl()).isEqualTo("https://existExample.com.jpg"),
                () -> assertThat(images.get(1).getImageUrl()).isEqualTo("https://newExample.com.jpg")
        );
    }

    @DisplayName("본인 것이 아닌 스타카토를 수정하려고 하면 예외가 발생한다.")
    @Test
    void failToUpdateMomentOfOther() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);
        StaccatoRequest staccatoRequest = StaccatoRequestFixture.create(category.getId());

        // when & then
        assertThatThrownBy(() -> staccatoService.updateStaccatoById(staccato.getId(), staccatoRequest, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본인 것이 아닌 카테고리에 속하도록 스타카토를 수정하려고 하면 예외가 발생한다.")
    @Test
    void failToUpdateMomentToOtherCategory() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Category category = saveCategory(member);
        Category otherCategory = saveCategory(otherMember);
        Staccato staccato = saveStaccatoWithImages(category);
        StaccatoRequest staccatoRequest = StaccatoRequestFixture.create(otherCategory.getId());

        // when & then
        assertThatThrownBy(() -> staccatoService.updateStaccatoById(staccato.getId(), staccatoRequest, member))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 스타카토를 수정하면 예외가 발생한다.")
    @Test
    void failToUpdateNotExistMoment() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        StaccatoRequest staccatoRequest = StaccatoRequestFixture.create(category.getId());

        // when & then
        assertThatThrownBy(() -> staccatoService.updateStaccatoById(1L, staccatoRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("Moment을 삭제하면 이에 포함된 MomentImage와 MomentLog도 모두 삭제된다.")
    @Test
    void deleteStaccatoById() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);
        Comment comment = commentRepository.save(CommentFixture.create(staccato, member));

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
        Member member = saveMember();
        Member otherMember = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

        // when & then
        assertThatThrownBy(() -> staccatoService.deleteStaccatoById(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("스타카토의 기분을 선택할 수 있다.")
    @Test
    void updateStaccatoFeelingById() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);
        FeelingRequest feelingRequest = new FeelingRequest("happy");

        // when
        staccatoService.updateStaccatoFeelingById(staccato.getId(), member, feelingRequest);

        // then
        assertAll(
                () -> assertThat(staccatoRepository.findById(staccato.getId())).isNotEmpty(),
                () -> assertThat(staccatoRepository.findById(staccato.getId()).get().getFeeling()).isEqualTo(Feeling.HAPPY)
        );
    }

    private Member saveMember() {
        return memberRepository.save(MemberFixture.create());
    }

    private Category saveCategory(Member member) {
        Category category = CategoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        category.addCategoryMember(member);
        return categoryRepository.save(category);
    }

    private Staccato saveStaccatoWithImages(Category category) {
        Staccato staccato = StaccatoFixture.createWithImages(category, LocalDateTime.now(), List.of("https://oldExample.com.jpg", "https://existExample.com.jpg"));
        return staccatoRepository.save(staccato);
    }
}
