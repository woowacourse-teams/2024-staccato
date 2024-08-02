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
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitLog;
import com.staccato.visit.fixture.VisitFixture;
import com.staccato.visit.fixture.VisitLogFixture;
import com.staccato.visit.repository.VisitLogRepository;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.response.VisitDetailResponse;

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
    private MemberRepository memberRepository;

    @DisplayName("Visit을 삭제하면 이에 포함된 VisitLog도 모두 삭제된다.")
    @Test
    void deleteVisitById() {
        // given
        Member member = memberRepository.save(Member.builder().nickname("Sample Member").build());
        Travel travel = travelRepository.save(
                Travel.builder().title("Sample Travel").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1)).build()
        );
        Visit visit = visitRepository.save(VisitFixture.create(travel, LocalDate.now()));
        VisitLog visitLog = visitLogRepository.save(VisitLogFixture.create(visit, member));

        // when
        visitService.deleteVisitById(visit.getId());

        // then
        assertAll(
                () -> assertThat(visitRepository.findById(visit.getId())).isEmpty(),
                () -> assertThat(visitLogRepository.findById(visitLog.getId())).isEmpty()
        );
    }

    @DisplayName("특정 방문 기록 조회에 성공한다.")
    @Test
    void readVisitById() {
        // given
        Travel travel = travelRepository.save(
                Travel.builder().title("Sample Travel").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1)).build()
        );
        Visit visit = visitRepository.save(VisitFixture.create(travel, LocalDate.now()));

        // when & then
        assertThat(visitService.readVisitById(visit.getId())).isEqualTo(new VisitDetailResponse(visit));
    }

    @DisplayName("존재하지 않는 방문 기록을 조회하면 예외가 발생한다.")
    @Test
    void failReadVisitById() {
        // given & when & then
        assertThatThrownBy(() -> visitService.readVisitById(1L))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 방문 기록을 찾을 수 없어요.");
    }
}
