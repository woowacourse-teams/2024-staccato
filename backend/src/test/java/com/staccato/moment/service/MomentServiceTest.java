package com.staccato.moment.service;

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
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.fixture.moment.MomentRequestFixture;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MomentServiceTest extends ServiceSliceTest {
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

    private Member saveMember() {
        return memberRepository.save(MemberFixture.create());
    }

    private Memory saveMemory(Member member) {
        Memory memory = MemoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        memory.addMemoryMember(member);
        return memoryRepository.save(memory);
    }

    private Moment saveMomentWithImages(Memory memory) {
        Moment moment = MomentFixture.createWithImages(memory, LocalDateTime.now(), List.of("https://oldExample.com.jpg", "https://existExample.com.jpg"));
        return momentRepository.save(moment);
    }
}
