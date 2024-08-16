package com.staccato.moment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import com.staccato.ServiceSliceTest;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.repository.CommentRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.moment.CommentFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImages;
import com.staccato.moment.repository.MomentImageRepository;
import com.staccato.moment.repository.MomentRepository;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.MomentUpdateRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;

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

    @DisplayName("사진 없이도 순간을 생성할 수 있다.")
    @Test
    void createMoment() {
        // given
        Member member = saveMember();
        saveMemory(member);

        // when
        long momentId = momentService.createMoment(getMomentRequestWithoutImage(), List.of(), member).momentId();

        // then
        assertThat(momentRepository.findById(momentId)).isNotEmpty();
    }

    private MomentRequest getMomentRequestWithoutImage() {
        return new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), 1L);
    }

    @DisplayName("순간을 생성하면 Moment과 MomentImage들이 함께 저장되고 id를 반환한다.")
    @Test
    void createMomentWithMomentImages() {
        // given
        Member member = saveMember();
        saveMemory(member);

        // when
        long momentId = momentService.createMoment(getMomentRequest(), List.of(new MockMultipartFile("momentImageFiles", "example.jpg".getBytes())), member).momentId();

        // then
        assertAll(
                () -> assertThat(momentRepository.findById(momentId)).isNotEmpty(),
                () -> assertThat(momentImageRepository.findFirstByMomentId(momentId)).isNotEmpty()
        );
    }

    @DisplayName("본인 것이 아닌 추억에 순간을 생성하려고 하면 예외가 발생한다.")
    @Test
    void cannotCreateMomentIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        saveMemory(member);
        MomentRequest momentRequest = getMomentRequest();

        // when & then
        assertThatThrownBy(() -> momentService.createMoment(momentRequest, List.of(new MockMultipartFile("momentImageFiles", "example.jpg".getBytes())), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 추억에 순간 생성을 시도하면 예외가 발생한다.")
    @Test
    void failCreateMoment() {
        // given
        Member member = saveMember();

        // when & then
        assertThatThrownBy(() -> momentService.createMoment(getMomentRequest(), List.of(new MockMultipartFile("momentImageFiles", "example.jpg".getBytes())), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 추억을 찾을 수 없어요.");
    }

    private MomentRequest getMomentRequest() {
        return new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), 1L);
    }

    @DisplayName("특정 순간 조회에 성공한다.")
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

    @DisplayName("본인 것이 아닌 특정 순간을 조회하려고 하면 예외가 발생한다.")
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

    @DisplayName("존재하지 않는 순간을 조회하면 예외가 발생한다.")
    @Test
    void failReadMomentById() {
        // given
        Member member = saveMember();

        // when & then
        assertThatThrownBy(() -> momentService.readMomentById(1L, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 순간을 찾을 수 없어요.");
    }

    @DisplayName("특정 순간 수정에 성공한다.")
    @Test
    void updateMomentById() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);

        // when
        MomentUpdateRequest momentUpdateRequest = new MomentUpdateRequest("newPlaceName", List.of("https://existExample.com.jpg"));
        MockMultipartFile mockMultipartFile = new MockMultipartFile("momentImagesFile", "newExample.jpg".getBytes());
        momentService.updateMomentById(moment.getId(), momentUpdateRequest, List.of(mockMultipartFile), member);

        // then
        Moment foundedMoment = momentRepository.findById(moment.getId()).get();
        assertAll(
                () -> assertThat(foundedMoment.getPlaceName()).isEqualTo("newPlaceName"),
                () -> assertThat(momentImageRepository.findById(1L)).isEmpty(),
                () -> assertThat(momentImageRepository.findById(2L).get().getImageUrl()).isEqualTo("https://existExample.com.jpg"),
                () -> assertThat(momentImageRepository.findById(3L).get().getImageUrl()).isEqualTo("fakeUrl"),
                () -> assertThat(momentImageRepository.findById(2L).get().getMoment().getId()).isEqualTo(foundedMoment.getId()),
                () -> assertThat(momentImageRepository.findById(3L).get().getMoment().getId()).isEqualTo(foundedMoment.getId()),
                () -> assertThat(momentImageRepository.findAll().size()).isEqualTo(2)
        );
    }

    @DisplayName("본인 것이 아닌 특정 순간을 수정하려고 하면 예외가 발생한다.")
    @Test
    void cannotUpdateMomentByIdIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);
        MomentUpdateRequest momentUpdateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));

        // when & then
        assertThatThrownBy(() -> momentService.updateMomentById(moment.getId(), momentUpdateRequest, List.of(new MockMultipartFile("momentImagesFile", "namsan_tower.jpg".getBytes())), otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 순간을 수정하면 예외가 발생한다.")
    @Test
    void failUpdateMomentById() {
        // given
        Member member = saveMember();
        MomentUpdateRequest momentUpdateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));

        // when & then
        assertThatThrownBy(() -> momentService.updateMomentById(1L, momentUpdateRequest, List.of(new MockMultipartFile("momentImagesFile", "namsan_tower.jpg".getBytes())), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 순간을 찾을 수 없어요.");
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

    @DisplayName("본인 것이 아닌 특정 순간을 삭제하려고 하면 예외가 발생한다.")
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

    private Member saveMember() {
        return memberRepository.save(Member.builder().nickname("staccato").build());
    }

    private Memory saveMemory(Member member) {
        Memory memory = Memory.builder().title("Sample Memory").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1)).build();
        memory.addMemoryMember(member);

        return memoryRepository.save(memory);
    }

    private Moment saveMomentWithImages(Memory memory) {
        Moment moment = MomentFixture.create(memory, LocalDateTime.now());
        moment.addMomentImages(new MomentImages(List.of("https://oldExample.com.jpg", "https://existExample.com.jpg")));
        return momentRepository.save(moment);
    }
}
