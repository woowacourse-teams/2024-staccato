package com.staccato.visit.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.pin.domain.Pin;
import com.staccato.pin.repository.PinRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitLog;
import com.staccato.visit.repository.VisitLogRepository;
import com.staccato.visit.repository.VisitRepository;

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
}
