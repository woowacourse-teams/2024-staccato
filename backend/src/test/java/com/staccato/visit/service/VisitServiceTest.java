package com.staccato.visit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.pin.domain.Pin;
import com.staccato.pin.repository.PinRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImage;
import com.staccato.visit.domain.VisitLog;
import com.staccato.visit.repository.VisitImageRepository;
import com.staccato.visit.repository.VisitLogRepository;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.response.VisitDetailResponse;
import com.staccato.visit.service.dto.response.VisitLogResponse;

class VisitServiceTest extends ServiceSliceTest {
    @Autowired
    private VisitService visitService;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private VisitLogRepository visitLogRepository;
    @Autowired
    private TravelRepository travelRepository;
    @Autowired
    private PinRepository pinRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private VisitImageRepository visitImageRepository;

    @DisplayName("Visit을 삭제하면 이에 포함된 VisitLog들도 모두 삭제된다.")
    @Test
    void deleteById() {
        // given
        Pin pin = pinRepository.save(Pin.builder().place("Sample Place").address("Sample Address").build());
        Travel travel = travelRepository.save(
                Travel.builder().title("Sample Travel").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1)).build()
        );
        Visit visit = visitRepository.save(Visit.builder().visitedAt(LocalDate.now()).pin(pin).travel(travel).build());
        Member member = memberRepository.save(Member.builder().nickname("Sample Member").build());
        VisitLog visitLog = visitLogRepository.save(VisitLog.builder().content("Sample Visit Log").visit(visit).member(member).build());

        // when
        visitService.deleteById(visit.getId());

        // then
        assertThat(visitRepository.findById(visit.getId()).get().getIsDeleted()).isTrue();
        assertThat(visitLogRepository.findById(visitLog.getId()).get().getIsDeleted()).isTrue();
    }

    @DisplayName("특정 방문 상세를 조회한다.")
    @Test
    void getById() {
        // given
        Pin pin = pinRepository.save(Pin.builder().place("Sample Place").address("Sample Address").build());
        Travel travel = travelRepository.save(Travel.builder().title("Sample Travel").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1)).build());
        Visit visit = visitRepository.save(Visit.builder().visitedAt(LocalDate.now()).pin(pin).travel(travel).build());
        Member member = memberRepository.save(Member.builder().nickname("Sample Member").build());
        VisitLog visitLog = visitLogRepository.save(VisitLog.builder().content("Sample Visit Log").visit(visit).member(member).build());
        VisitImage visitImage = visitImageRepository.save(VisitImage.builder().imageUrl("Sample URL").visit(visit).build());

        // when
        VisitDetailResponse visitDetailResponse = visitService.getById(visit.getId());

        // then
        assertAll(
                () -> assertThat(visitDetailResponse.visitId()).isEqualTo(visit.getId()),
                () -> assertThat(visitDetailResponse.placeName()).isEqualTo(pin.getPlace()),
                () -> assertThat(visitDetailResponse.visitedImages()).containsExactly(visitImage.getImageUrl()),
                () -> assertThat(visitDetailResponse.address()).isEqualTo(pin.getAddress()),
                () -> assertThat(visitDetailResponse.visitedAt()).isEqualTo(visit.getVisitedAt()),
                () -> assertThat(visitDetailResponse.visitedCount()).isEqualTo(1L),
                () -> assertThat(visitDetailResponse.visitLogs()).containsExactly(new VisitLogResponse(visitLog))
        );
    }

    @DisplayName("존재하지 않는 방문 기록을 조회하면 예외가 발생한다.")
    @Test
    void failGetByPin() {
        assertThatThrownBy(() -> visitService.getById(1L))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 방문 기록을 찾을 수 없어요.");
    }
}
