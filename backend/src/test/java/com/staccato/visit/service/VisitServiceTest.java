package com.staccato.visit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
import com.staccato.visit.repository.VisitImageRepository;
import com.staccato.visit.repository.VisitLogRepository;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.request.VisitRequest;

class VisitServiceTest extends ServiceSliceTest {
    @Autowired
    private VisitService visitService;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private VisitLogRepository visitLogRepository;
    @Autowired
    private VisitImageRepository visitImageRepository;
    @Autowired
    private TravelRepository travelRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("방문 기록을 생성하면 Visit과 VisitImage들이 함께 저장되고 id를 반환한다.")
    @Test
    void createVisit() {
        // given
        saveTravel();

        // when
        long visitId = visitService.createVisit(getVisitRequest()).visitId();

        // then
        assertAll(
                () -> assertThat(visitRepository.findById(visitId)).isNotEmpty(),
                () -> assertThat(visitImageRepository.findAllByVisitId(visitId)).hasSize(2)
        );
    }

    @DisplayName("존재하지 않는 여행에 방문 기록 생성을 시도하면 예외가 발생한다.")
    @Test
    void failCreateVisit() {
        assertThatThrownBy(() -> visitService.createVisit(getVisitRequest())).isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 여행을 찾을 수 없어요.");
    }

    private VisitRequest getVisitRequest() {
        return new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, List.of("https://example1.com.jpg", "https://example2.com.jpg"), LocalDate.now(), 1L);
    }

    @DisplayName("Visit을 삭제하면 이에 포함된 VisitLog도 모두 삭제된다.")
    @Test
    void deleteVisitById() {
        // given
        Travel travel = saveTravel();
        Visit visit = visitRepository.save(Visit.builder()
                .visitedAt(LocalDate.now())
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .travel(travel)
                .build());
        Member member = memberRepository.save(Member.builder().nickname("Sample Member").build());
        VisitLog visitLog = visitLogRepository.save(VisitLog.builder().content("Sample Visit Log").visit(visit)
                .member(member).build());

        // when
        visitService.deleteVisitById(visit.getId());

        // then
        assertAll(
                () -> assertThat(visitRepository.findById(visit.getId())).isEmpty(),
                () -> assertThat(visitLogRepository.findById(visitLog.getId())).isEmpty()
        );
    }

    private Travel saveTravel() {
        Travel travel = Travel.builder()
                .title("Sample Travel")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1))
                .build();

        return travelRepository.save(travel);
    }

    @DisplayName("존재하지 않는 방문 기록을 조회하면 예외가 발생한다.")
    @Test
    void failReadVisitById() {
        assertThatThrownBy(() -> visitService.readVisitById(1L))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 방문 기록을 찾을 수 없어요.");
    }
}
