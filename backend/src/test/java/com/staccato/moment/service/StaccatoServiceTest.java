package com.staccato.moment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import com.staccato.config.jwt.ShareTokenProvider;
import com.staccato.config.jwt.TokenProperties;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.exception.UnauthorizedException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.comment.CommentFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.fixture.moment.MomentRequestFixture;
import com.staccato.fixture.staccato.StaccatoSharedResponseFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Feeling;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImage;
import com.staccato.moment.domain.MomentImages;
import com.staccato.moment.repository.MomentImageRepository;
import com.staccato.moment.repository.MomentRepository;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;
import com.staccato.moment.service.dto.response.StaccatoShareLinkResponse;
import com.staccato.moment.service.dto.response.StaccatoSharedResponse;
import com.staccato.util.StubTokenProvider;

import io.jsonwebtoken.Claims;

class StaccatoServiceTest extends ServiceSliceTest {

    @Autowired
    private MomentService momentService;
    @Autowired
    private MomentRepository momentRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MomentImageRepository momentImageRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StaccatoService staccatoService;
    @Autowired
    private ShareTokenProvider shareTokenProvider;

    @DisplayName("사진 없이도 스타카토를 생성할 수 있다.")
    @Test
    void createMoment() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        MomentRequest momentRequest = MomentRequestFixture.create(memory.getId());

        // when
        long momentId = momentService.createMoment(momentRequest, member).momentId();

        // then
        assertThat(momentRepository.findById(momentId)).isNotEmpty();
    }

    @DisplayName("스타카토를 생성하면 Moment과 MomentImage들이 함께 저장되고 id를 반환한다.")
    @Test
    void createMomentWithMomentImages() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        MomentRequest momentRequest = MomentRequestFixture.create(memory.getId(), List.of("image.jpg"));

        // when
        long momentId = momentService.createMoment(momentRequest, member).momentId();

        // then
        assertAll(
                () -> assertThat(momentRepository.findById(momentId)).isNotEmpty(),
                () -> assertThat(momentImageRepository.findAll().size()).isEqualTo(1),
                () -> assertThat(momentImageRepository.findAll().get(0).getMoment().getId()).isEqualTo(momentId)
        );
    }

    @DisplayName("본인 것이 아닌 추억에 스타카토를 생성하려고 하면 예외가 발생한다.")
    @Test
    void cannotCreateMomentIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Memory memory = saveMemory(member);
        MomentRequest momentRequest = MomentRequestFixture.create(memory.getId());

        // when & then
        assertThatThrownBy(() -> momentService.createMoment(momentRequest, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 추억에 스타카토 생성을 시도하면 예외가 발생한다.")
    @Test
    void failCreateMoment() {
        // given
        Member member = saveMember();
        MomentRequest momentRequest = MomentRequestFixture.create(0L);

        // when & then
        assertThatThrownBy(() -> momentService.createMoment(momentRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 추억을 찾을 수 없어요.");
    }

    @DisplayName("스타카토 목록 조회에 성공한다.")
    @Test
    void readAllMoment() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        saveMomentWithImages(memory);
        saveMomentWithImages(memory);

        // when
        MomentLocationResponses actual = momentService.readAllMoment(member);

        // then
        assertThat(actual.momentLocationResponses()).hasSize(2);
    }

    @DisplayName("스타카토 조회에 성공한다.")
    @Test
    void readMomentById() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        // when
        MomentDetailResponse actual = momentService.readMomentById(moment.getId(), member);

        // then
        assertThat(actual).isEqualTo(new MomentDetailResponse(moment));
    }

    @DisplayName("본인 것이 아닌 스타카토를 조회하려고 하면 예외가 발생한다.")
    @Test
    void cannotReadMomentByIdIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        // when & then
        assertThatThrownBy(() -> momentService.readMomentById(moment.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 스타카토를 조회하면 예외가 발생한다.")
    @Test
    void failReadMomentById() {
        // given
        Member member = saveMember();

        // when & then
        assertThatThrownBy(() -> momentService.readMomentById(1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("스타카토 수정에 성공한다.")
    @Test
    void updateMomentById() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Memory memory2 = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        // when
        MomentRequest momentRequest = new MomentRequest("newStaccatoTitle", "placeName", "newAddress", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), memory2.getId(), List.of("https://existExample.com.jpg", "https://newExample.com.jpg"));
        momentService.updateMomentById(moment.getId(), momentRequest, member);

        // then
        Moment foundedMoment = momentRepository.findById(moment.getId()).get();
        List<MomentImage> images = momentImageRepository.findAll();
        assertAll(
                () -> assertThat(foundedMoment.getTitle()).isEqualTo("newStaccatoTitle"),
                () -> assertThat(foundedMoment.getMemory().getId()).isEqualTo(memory2.getId()),
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
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);
        MomentRequest momentRequest = MomentRequestFixture.create(memory.getId());

        // when & then
        assertThatThrownBy(() -> momentService.updateMomentById(moment.getId(), momentRequest, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("본인 것이 아닌 추억에 속하도록 스타카토를 수정하려고 하면 예외가 발생한다.")
    @Test
    void failToUpdateMomentToOtherMemory() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Memory memory = saveMemory(member);
        Memory otherMemory = saveMemory(otherMember);
        Moment moment = saveMomentWithImages(memory);
        MomentRequest momentRequest = MomentRequestFixture.create(otherMemory.getId());

        // when & then
        assertThatThrownBy(() -> momentService.updateMomentById(moment.getId(), momentRequest, member))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 스타카토를 수정하면 예외가 발생한다.")
    @Test
    void failToUpdateNotExistMoment() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        MomentRequest momentRequest = MomentRequestFixture.create(memory.getId());

        // when & then
        assertThatThrownBy(() -> momentService.updateMomentById(1L, momentRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("Moment을 삭제하면 이에 포함된 MomentImage와 MomentLog도 모두 삭제된다.")
    @Test
    void deleteMomentById() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);
        Comment comment = commentRepository.save(CommentFixture.create(moment, member));

        // when
        momentService.deleteMomentById(moment.getId(), member);

        // then
        assertAll(
                () -> assertThat(momentRepository.findById(moment.getId())).isEmpty(),
                () -> assertThat(commentRepository.findById(comment.getId())).isEmpty(),
                () -> assertThat(momentImageRepository.findById(0L)).isEmpty(),
                () -> assertThat(momentImageRepository.findById(1L)).isEmpty()
        );
    }

    @DisplayName("본인 것이 아닌 스타카토를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteMomentByIdIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        // when & then
        assertThatThrownBy(() -> momentService.deleteMomentById(moment.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("스타카토의 기분을 선택할 수 있다.")
    @Test
    void updateMomentFeelingById() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);
        FeelingRequest feelingRequest = new FeelingRequest("happy");

        // when
        momentService.updateMomentFeelingById(moment.getId(), member, feelingRequest);

        // then
        assertAll(
                () -> assertThat(momentRepository.findById(moment.getId())).isNotEmpty(),
                () -> assertThat(momentRepository.findById(moment.getId()).get().getFeeling()).isEqualTo(Feeling.HAPPY)
        );
    }

    @DisplayName("올바른 형식의 공유 링크를 생성할 수 있다.")
    @Test
    void createValidShareLink() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        // when
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(moment.getId(), member);

        // then
        assertThat(response.shareLink()).startsWith("https://staccato.kr/share?token=");
    }

    @DisplayName("존재하지 않는 스타카토의 공유 링크를 생성하려고 하면, 예외가 발생한다.")
    @Test
    void failToCreateShareLinkWhenNoStaccato() {
        // given
        Member member = saveMember();

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccatoShareLink(1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 스타카토를 찾을 수 없어요.");
    }

    @DisplayName("본인 것이 아닌 스타카토의 공유 링크를 생성하려고 하면, 예외가 발생한다.")
    @Test
    void failToCreateShareLinkWhenInvalidMember() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        // when & then
        assertThatThrownBy(() -> staccatoService.createStaccatoShareLink(moment.getId(), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("공유 링크는 JWT 토큰을 포함하고 있다.")
    @Test
    void shouldContainTokenInShareLink() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        // when
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(moment.getId(), member);
        String token = response.shareLink().split("token=")[1];

        // then
        assertThat(token).isNotNull();
    }

    @DisplayName("토큰은 올바른 스타카토 id를 포함하고 있다.")
    @Test
    void shouldContainStaccatoIdInToken() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(moment.getId(), member);
        String token = response.shareLink().split("token=")[1];

        // when & then
        assertThatCode(() -> shareTokenProvider.extractStaccatoId(token))
                .doesNotThrowAnyException();
        assertThat(shareTokenProvider.extractStaccatoId(token)).isEqualTo(moment.getId());
    }

    @DisplayName("토큰은 만료 기한을 포함하고 있다.")
    @Test
    void shouldContainExpirationInToken() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(moment.getId(), member);
        String token = response.shareLink().split("token=")[1];

        // when
        Claims claims = shareTokenProvider.getPayload(token);

        // then
        assertThat(claims.getExpiration()).isNotNull();
    }

    @DisplayName("토큰이 유효하다.")
    @Test
    void validateToken() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(moment.getId(), member);
        String token = response.shareLink().split("token=")[1];

        // when & then
        assertThatCode(() -> shareTokenProvider.validateToken(token))
                .doesNotThrowAnyException();
    }

    @DisplayName("토큰에서 스타카토 id를 추출해서, 해당 스타카토 조회를 성공한다.")
    @Test
    void readSharedStaccatoByToken() {
        // given
        Member member1 = saveMemberWithNicknameAndImageUrl("staccato", "image.jpg");
        Member member2 = saveMemberWithNicknameAndImageUrl("staccato2", "image2.jpg");
        Memory memory = saveMemoryWithFixedDate(member1);
        Moment moment = saveMomentWithFixedDateTime(memory);
        saveComment(moment, member1, "댓글 샘플");
        saveComment(moment, member2, "댓글 샘플2");

        String token = shareTokenProvider.create(moment.getId());

        // when
        StaccatoSharedResponse response = staccatoService.readSharedStaccatoByToken(token);

        // then
        assertThat(response).isEqualTo(StaccatoSharedResponseFixture.create());
    }

    @DisplayName("만료된 토큰이면, 예외가 발생한다.")
    @Test
    void failReadSharedStaccatoWhenTokenExpired() {
        // given
        TokenProperties tokenProperties = new TokenProperties("test-secret-key");
        StubTokenProvider stubTokenProvider = new StubTokenProvider(tokenProperties);
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
        String token = shareTokenProvider.create(1L);

        // when & then
        assertThatThrownBy(() -> staccatoService.readSharedStaccatoByToken(token))
                .isInstanceOf(StaccatoException.class);
    }

    private Member saveMember() {
        return memberRepository.save(MemberFixture.create());
    }

    private Member saveMemberWithNicknameAndImageUrl(String nickname, String imageUrl) {
        return memberRepository.save(MemberFixture.create(nickname, imageUrl));
    }

    private Memory saveMemory(Member member) {
        Memory memory = MemoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        memory.addMemoryMember(member);
        return memoryRepository.save(memory);
    }

    private Moment saveMomentWithImages(Memory memory) {
        Moment moment = MomentFixture.createWithImages(memory, LocalDateTime.now(), new MomentImages(List.of("https://oldExample.com.jpg", "https://existExample.com.jpg")));
        return momentRepository.save(moment);
    }

    private Memory saveMemoryWithFixedDate(Member member) {
        Memory memory = MemoryFixture.create(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 2));
        memory.addMemoryMember(member);
        return memoryRepository.save(memory);
    }

    private Moment saveMomentWithFixedDateTime(Memory memory) {
        Moment moment = MomentFixture.createWithImages(memory, LocalDateTime.of(2024, 7, 1, 10, 0), new MomentImages(List.of("https://oldExample.com.jpg", "https://existExample.com.jpg")));
        return momentRepository.save(moment);
    }

    private void saveComment(Moment moment, Member member, String content) {
        commentRepository.save(CommentFixture.create(moment, member, content));
    }
}
