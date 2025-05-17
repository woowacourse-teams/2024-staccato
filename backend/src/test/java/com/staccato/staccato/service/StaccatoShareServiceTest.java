package com.staccato.staccato.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.jwt.ShareTokenProvider;
import com.staccato.config.jwt.dto.ShareTokenPayload;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.exception.UnauthorizedException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.repository.StaccatoRepository;
import com.staccato.staccato.service.dto.response.StaccatoShareLinkResponse;
import com.staccato.staccato.service.dto.response.StaccatoSharedResponse;
import com.staccato.util.StubTokenProvider;

import io.jsonwebtoken.Claims;

public class StaccatoShareServiceTest extends ServiceSliceTest {
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ShareTokenProvider shareTokenProvider;
    @Autowired
    private StaccatoShareService staccatoShareService;

    @DisplayName("올바른 형식의 공유 링크를 생성할 수 있다.")
    @Test
    void createValidShareLink() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        StaccatoShareLinkResponse response = staccatoShareService.createStaccatoShareLink(staccato.getId(), member);

        // then
        assertThat(response.shareLink()).startsWith("http://localhost:8080/share/");
    }

    @DisplayName("존재하지 않는 스타카토의 공유 링크를 생성하려고 하면, 예외가 발생한다.")
    @Test
    void failToCreateShareLinkWhenNoStaccato() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);

        // when & then
        assertThatThrownBy(() -> staccatoShareService.createStaccatoShareLink(1L, member))
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
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when & then
        assertThatThrownBy(() -> staccatoShareService.createStaccatoShareLink(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("공유 링크는 JWT 토큰을 포함하고 있다.")
    @Test
    void shouldContainTokenInShareLink() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        // when
        StaccatoShareLinkResponse response = staccatoShareService.createStaccatoShareLink(staccato.getId(), member);

        // then
        assertThat(response.token()).isNotNull();
    }

    @DisplayName("토큰은 올바른 스타카토 id를 포함하고 있다.")
    @Test
    void shouldContainStaccatoIdInToken() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        StaccatoShareLinkResponse response = staccatoShareService.createStaccatoShareLink(staccato.getId(), member);

        // when & then
        assertThatCode(() -> shareTokenProvider.extractStaccatoId(response.token()))
                .doesNotThrowAnyException();
        assertThat(shareTokenProvider.extractStaccatoId(response.token())).isEqualTo(staccato.getId());
    }

    @DisplayName("토큰은 만료 기한을 포함하고 있다.")
    @Test
    void shouldContainExpirationInToken() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        StaccatoShareLinkResponse response = staccatoShareService.createStaccatoShareLink(staccato.getId(), member);

        // when
        Claims claims = shareTokenProvider.getPayload(response.token());

        // then
        assertThat(claims.getExpiration()).isNotNull();
    }

    @DisplayName("토큰이 유효하다.")
    @Test
    void validateToken() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(member)
                .buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).buildAndSave(staccatoRepository);

        StaccatoShareLinkResponse response = staccatoShareService.createStaccatoShareLink(staccato.getId(), member);

        // when & then
        assertThatCode(() -> shareTokenProvider.validateToken(response.token()))
                .doesNotThrowAnyException();
    }

    @DisplayName("토큰에서 스타카토 id를 추출해서, 해당 스타카토 조회를 성공한다.")
    @Test
    void readSharedStaccatoByToken() {
        // given
        Member member1 = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Member member2 = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withHost(member1).buildAndSave(categoryRepository);
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
        StaccatoSharedResponse actual = staccatoShareService.readSharedStaccatoByToken(token);
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
        assertThatThrownBy(() -> staccatoShareService.readSharedStaccatoByToken(expiredToken))
                .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("잘못된 토큰이면, 예외가 발생한다.")
    @Test
    void failReadSharedStaccatoWhenTokenInvalid() {
        // given
        String invalidToken = "invalid.token.value";

        // when & then
        assertThatThrownBy(() -> staccatoShareService.readSharedStaccatoByToken(invalidToken))
                .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("해당하는 스타카토가 없으면, 예외가 발생한다.")
    @Test
    void failReadSharedStaccatoWhenStaccatoNotExist() {
        // given
        String token = shareTokenProvider.create(new ShareTokenPayload(1L, 1L));

        // when & then
        assertThatThrownBy(() -> staccatoShareService.readSharedStaccatoByToken(token))
                .isInstanceOf(StaccatoException.class);
    }
}
