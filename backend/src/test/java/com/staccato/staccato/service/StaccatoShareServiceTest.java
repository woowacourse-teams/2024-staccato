package com.staccato.staccato.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.config.jwt.ShareTokenProvider;
import com.staccato.config.jwt.dto.ShareTokenPayload;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.exception.UnauthorizedException;
import com.staccato.fixture.category.CategoryFixture;
import com.staccato.fixture.comment.CommentFixture;
import com.staccato.fixture.member.MemberFixture;
import com.staccato.fixture.staccato.StaccatoFixture;
import com.staccato.fixture.staccato.StaccatoSharedResponseFixture;
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
        Member member = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

        // when
        StaccatoShareLinkResponse response = staccatoShareService.createStaccatoShareLink(staccato.getId(), member);

        // then
        assertThat(response.shareLink()).startsWith("http://localhost:8080/share/");
    }

    @DisplayName("존재하지 않는 스타카토의 공유 링크를 생성하려고 하면, 예외가 발생한다.")
    @Test
    void failToCreateShareLinkWhenNoStaccato() {
        // given
        Member member = saveMember();

        // when & then
        assertThatThrownBy(() -> staccatoShareService.createStaccatoShareLink(1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("본인 것이 아닌 스타카토의 공유 링크를 생성하려고 하면, 예외가 발생한다.")
    @Test
    void failToCreateShareLinkWhenInvalidMember() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

        // when & then
        assertThatThrownBy(() -> staccatoShareService.createStaccatoShareLink(staccato.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("공유 링크는 JWT 토큰을 포함하고 있다.")
    @Test
    void shouldContainTokenInShareLink() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

        // when
        StaccatoShareLinkResponse response = staccatoShareService.createStaccatoShareLink(staccato.getId(), member);

        // then
        assertThat(response.token()).isNotNull();
    }

    @DisplayName("토큰은 올바른 스타카토 id를 포함하고 있다.")
    @Test
    void shouldContainStaccatoIdInToken() {
        // given
        Member member = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

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
        Member member = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

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
        Member member = saveMember();
        Category category = saveCategory(member);
        Staccato staccato = saveStaccatoWithImages(category);

        StaccatoShareLinkResponse response = staccatoShareService.createStaccatoShareLink(staccato.getId(), member);

        // when & then
        assertThatCode(() -> shareTokenProvider.validateToken(response.token()))
                .doesNotThrowAnyException();
    }

    @DisplayName("토큰에서 스타카토 id를 추출해서, 해당 스타카토 조회를 성공한다.")
    @Test
    void readSharedStaccatoByToken() {
        // given
        Member member1 = saveMemberWithNicknameAndImageUrl("staccato", "image.jpg");
        Member member2 = saveMemberWithNicknameAndImageUrl("staccato2", "image2.jpg");
        Category category = saveCategoryWithFixedDate(member1);
        Staccato staccato = saveStaccatoWithFixedDateTime(category);
        saveComment(staccato, member1, "댓글 샘플");
        saveComment(staccato, member2, "댓글 샘플2");

        String token = shareTokenProvider.create(new ShareTokenPayload(staccato.getId(), member1.getId()));

        // when
        StaccatoSharedResponse response = staccatoShareService.readSharedStaccatoByToken(token);

        // then
        assertThat(response).isEqualTo(StaccatoSharedResponseFixture.create(response.expiredAt()));
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

    private Member saveMember() {
        return memberRepository.save(MemberFixture.create());
    }

    private Member saveMemberWithNicknameAndImageUrl(String nickname, String imageUrl) {
        return memberRepository.save(MemberFixture.create(nickname, imageUrl));
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

    private Category saveCategoryWithFixedDate(Member member) {
        Category category = CategoryFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 2));
        category.addCategoryMember(member);
        return categoryRepository.save(category);
    }

    private Staccato saveStaccatoWithFixedDateTime(Category category) {
        Staccato staccato = StaccatoFixture.createWithImages(category, LocalDateTime.of(2024, 7, 1, 10, 0), List.of("https://oldExample.com.jpg", "https://existExample.com.jpg"));
        return staccatoRepository.save(staccato);
    }

    private void saveComment(Staccato staccato, Member member, String content) {
        commentRepository.save(CommentFixture.create(staccato, member, content));
    }
}
