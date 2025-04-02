package com.staccato.staccato.service;

import com.staccato.category.domain.Category;
import com.staccato.config.jwt.ShareTokenProvider;
import com.staccato.config.jwt.dto.ShareTokenPayload;
import com.staccato.exception.UnauthorizedException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.fixture.staccato.StaccatoRequestFixtures;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.service.dto.request.StaccatoRequest;
import com.staccato.staccato.service.dto.response.StaccatoDetailResponse;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponses;
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
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.staccato.domain.Feeling;
import com.staccato.staccato.domain.StaccatoImage;
import com.staccato.staccato.repository.StaccatoImageRepository;
import com.staccato.staccato.repository.StaccatoRepository;
import com.staccato.staccato.service.dto.request.FeelingRequest;
import com.staccato.staccato.service.dto.response.StaccatoShareLinkResponse;
import com.staccato.staccato.service.dto.response.StaccatoSharedResponse;
import com.staccato.util.StubTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.jsonwebtoken.Claims;

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
    @Autowired
    private ShareTokenProvider shareTokenProvider;

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
                .buildAndSaveWithMember(member, categoryRepository);
        StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        StaccatoLocationResponses actual = staccatoService.readAllStaccato(member);

        // then
        assertThat(actual.staccatoLocationResponses()).hasSize(2);
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
                .withStaccatoImageUrls(List.of("https://example.com/staccatoImage2.jpg", "https://example.com/staccatoImage3.jpg")).build();
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
                () -> assertThat(staccatoRepository.findById(staccato.getId()).get().getFeeling()).isEqualTo(Feeling.HAPPY)
        );
    }

    @DisplayName("올바른 형식의 공유 링크를 생성할 수 있다.")
    @Test
    void createValidShareLink() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(staccato.getId(), member);

        // then
        assertThat(response.shareLink()).startsWith("https://staccato.kr/share/");
    }

    @DisplayName("존재하지 않는 스타카토의 공유 링크를 생성하려고 하면, 예외가 발생한다.")
    @Test
    void failToCreateShareLinkWhenNoStaccato() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccatoShareLink(1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("본인 것이 아닌 스타카토의 공유 링크를 생성하려고 하면, 예외가 발생한다.")
    @Test
    void failToCreateShareLinkWhenInvalidMember() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member otherMember = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccatoShareLink(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("공유 링크는 JWT 토큰을 포함하고 있다.")
    @Test
    void shouldContainTokenInShareLink() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(staccato.getId(), member);
        String token = response.getToken();

        // then
        assertThat(token).isNotNull();
    }

    @DisplayName("토큰은 올바른 스타카토 id를 포함하고 있다.")
    @Test
    void shouldContainStaccatoIdInToken() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(staccato.getId(), member);
        String token = response.getToken();

        // when & then
        assertThatCode(() -> shareTokenProvider.extractStaccatoId(token))
                .doesNotThrowAnyException();
        assertThat(shareTokenProvider.extractStaccatoId(token)).isEqualTo(staccato.getId());
    }

    @DisplayName("토큰은 만료 기한을 포함하고 있다.")
    @Test
    void shouldContainExpirationInToken() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(staccato.getId(), member);
        String token = response.getToken();

        // when
        Claims claims = shareTokenProvider.getPayload(token);

        // then
        assertThat(claims.getExpiration()).isNotNull();
    }

    @DisplayName("토큰이 유효하다.")
    @Test
    void validateToken() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member, categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(staccato.getId(), member);
        String token = response.getToken();

        // when & then
        assertThatCode(() -> shareTokenProvider.validateToken(token))
                .doesNotThrowAnyException();
    }

    @DisplayName("토큰에서 스타카토 id를 추출해서, 해당 스타카토 조회를 성공한다.")
    @Test
    void readSharedStaccatoByToken() {
        // given
        Member member1 = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member member2 = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .buildAndSaveWithMember(member1, categoryRepository);

        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);
        Comment comment1 = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(member1).buildAndSave(commentRepository);
        Comment comment2 = CommentFixtures.defaultComment()
                .withStaccato(staccato)
                .withMember(member2).buildAndSave(commentRepository);

        String token = shareTokenProvider.create(new ShareTokenPayload(staccato.getId(), member1.getId()));

        // when
        StaccatoSharedResponse actual = staccatoService.readSharedStaccatoByToken(token);
        StaccatoSharedResponse expected = new StaccatoSharedResponse(actual.expiredAt(), staccato, member1, List.of(comment1, comment2));

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("만료된 토큰이면, 예외가 발생한다.")
    @Test
    void failReadSharedStaccatoWhenTokenExpired() {
        // given
        StubTokenProvider stubTokenProvider = StubTokenProvider.withTestKey();
        String expiredToken = stubTokenProvider.createExpired("test-payload");

        // when & then
        assertThatThrownBy(() -> staccatoService.readSharedStaccatoByToken(expiredToken))
                .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("잘못된 토큰이면, 예외가 발생한다.")
    @Test
    void failReadSharedStaccatoWhenTokenInvalid() {
        // given
        String invalidToken = "invalid.token.value";

        // when & then
        assertThatThrownBy(() -> staccatoService.readSharedStaccatoByToken(invalidToken))
                .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("해당하는 스타카토가 없으면, 예외가 발생한다.")
    @Test
    void failReadSharedStaccatoWhenStaccatoNotExist() {
        // given
        String token = shareTokenProvider.create(new ShareTokenPayload(1L, 1L));

        // when & then
        assertThatThrownBy(() -> staccatoService.readSharedStaccatoByToken(token))
                .isInstanceOf(StaccatoException.class);
    }
}
