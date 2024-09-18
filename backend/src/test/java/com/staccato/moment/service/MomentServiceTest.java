package com.staccato.moment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Feeling;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImages;
import com.staccato.moment.repository.MomentImageRepository;
import com.staccato.moment.repository.MomentRepository;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.MomentUpdateRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;

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
        saveMemory(member);
        MomentRequest momentRequest = new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), 1L, List.of());

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
        saveMemory(member);

        // when
        long momentId = momentService.createMoment(getMomentRequest(), member).momentId();

        // then
        assertAll(
                () -> assertThat(momentRepository.findById(momentId)).isNotEmpty(),
                () -> assertThat(momentImageRepository.findFirstByMomentId(momentId)).isNotEmpty()
        );
    }

    @DisplayName("본인 것이 아닌 추억에 스타카토를 생성하려고 하면 예외가 발생한다.")
    @Test
    void cannotCreateMomentIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        saveMemory(member);
        MomentRequest momentRequest = getMomentRequest();

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

        // when & then
        assertThatThrownBy(() -> momentService.createMoment(getMomentRequest(), member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 추억을 찾을 수 없어요.");
    }

    private MomentRequest getMomentRequest() {
        return new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), 1L, List.of("https://example.com/images/namsan_tower.jpg"));
    }

    @DisplayName("스타카토 목록 조회에 성공한다.")
    @Test
    void readAllMoment() {
        // given
        Member member = saveMember();
        Memory memory = saveMemory(member);
        saveMomentWithImages(memory);
        saveMomentWithImages(memory);
        saveMomentWithImages(memory);

        // when
        MomentLocationResponses actual = momentService.readAllMoment(member);

        // then
        assertThat(actual).isEqualTo(new MomentLocationResponses(
                List.of(new MomentLocationResponse(1L, new BigDecimal("37.7749").setScale(14, RoundingMode.HALF_UP), new BigDecimal("-122.4194").setScale(14, RoundingMode.HALF_UP)),
                        new MomentLocationResponse(2L, new BigDecimal("37.7749").setScale(14, RoundingMode.HALF_UP), new BigDecimal("-122.4194").setScale(14, RoundingMode.HALF_UP)),
                        new MomentLocationResponse(3L, new BigDecimal("37.7749").setScale(14, RoundingMode.HALF_UP), new BigDecimal("-122.4194").setScale(14, RoundingMode.HALF_UP)))));
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
        Moment moment = saveMomentWithImages(memory);

        // when
        MomentUpdateRequest momentUpdateRequest = new MomentUpdateRequest("newPlaceName", List.of("https://existExample.com.jpg", "https://existExample2.com.jpg"));
        momentService.updateMomentById(moment.getId(), momentUpdateRequest, member);

        // then
        Moment foundedMoment = momentRepository.findById(moment.getId()).get();
        assertThat(foundedMoment.getPlaceName()).isEqualTo("newPlaceName");
    }

    @DisplayName("본인 것이 아닌 스타카토를 수정하려고 하면 예외가 발생한다.")
    @Test
    void cannotUpdateMomentByIdIfNotOwner() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();
        Memory memory = saveMemory(member);
        Moment moment = saveMomentWithImages(memory);
        MomentUpdateRequest momentUpdateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));

        // when & then
        assertThatThrownBy(() -> momentService.updateMomentById(moment.getId(), momentUpdateRequest, otherMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 스타카토를 수정하면 예외가 발생한다.")
    @Test
    void failUpdateMomentById() {
        // given
        Member member = saveMember();
        MomentUpdateRequest momentUpdateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));

        // when & then
        assertThatThrownBy(() -> momentService.updateMomentById(1L, momentUpdateRequest, member))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
    }

    @Nested
    @DisplayName("updateMomentByIdV2 테스트")
    class updateMomentByIdV2Test {

        @DisplayName("스타카토 수정에 성공한다.")
        @Test
        void updateMomentById() {
            // given
            Member member = saveMember();
            Memory memory = saveMemory(member);
            Memory memory2 = saveMemory(member);
            Moment moment = saveMomentWithImages(memory);

            // when
            MomentRequest momentRequest = new MomentRequest("newPlaceName", "newAddress", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), memory2.getId(), List.of("https://existExample.com.jpg", "https://existExample2.com.jpg"));
            momentService.updateMomentByIdV2(moment.getId(), momentRequest, member);

            // then
            Moment foundedMoment = momentRepository.findById(moment.getId()).get();
            assertThat(foundedMoment.getPlaceName()).isEqualTo("newPlaceName");
        }

        @DisplayName("본인 것이 아닌 스타카토를 수정하려고 하면 예외가 발생한다.")
        @Test
        void failToUpdateMomentOfOther() {
            // given
            Member member = saveMember();
            Member otherMember = saveMember();
            Memory memory = saveMemory(member);
            Moment moment = saveMomentWithImages(memory);
            MomentRequest momentRequest = new MomentRequest("newPlaceName", "newAddress", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), memory.getId(), List.of("https://existExample.com.jpg", "https://existExample2.com.jpg"));

            // when & then
            assertThatThrownBy(() -> momentService.updateMomentByIdV2(moment.getId(), momentRequest, otherMember))
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
            MomentRequest momentRequest = new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), otherMemory.getId(), List.of());

            // when & then
            assertThatThrownBy(() -> momentService.updateMomentByIdV2(moment.getId(), momentRequest, member))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
        }

        @DisplayName("존재하지 않는 스타카토를 수정하면 예외가 발생한다.")
        @Test
        void failToUpdateNotExistMoment() {
            // given
            Member member = saveMember();
            Memory memory = saveMemory(member);
            MomentRequest momentUpdateRequest = new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.now(), memory.getId(), List.of());

            // when & then
            assertThatThrownBy(() -> momentService.updateMomentByIdV2(1L, momentUpdateRequest, member))
                    .isInstanceOf(StaccatoException.class)
                    .hasMessageContaining("요청하신 스타카토를 찾을 수 없어요.");
        }
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
        Moment moment = MomentFixture.createWithImages(memory, LocalDateTime.now(), new MomentImages(List.of("https://oldExample.com.jpg", "https://existExample.com.jpg")));
        return momentRepository.save(moment);
    }
}
