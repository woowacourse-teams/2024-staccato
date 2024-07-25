package com.staccato.visit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.pin.domain.Pin;
import com.staccato.pin.repository.PinRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImage;

@DataJpaTest
class VisitImageRepositoryTest {
    @Autowired
    private VisitImageRepository visitImageRepository;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private TravelRepository travelRepository;
    @Autowired
    private PinRepository pinRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("특정 visitId를 갖는 visitImage들을 모두 삭제한다.")
    @Test
    void deleteByVisitId() {
        // given
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());
        Pin pin = pinRepository.save(Pin.builder().place("Sample Place").address("Sample Address").member(member).build());
        Travel travel = travelRepository.save(Travel.builder().title("Sample Travel").startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1)).build());
        Visit visit = visitRepository.save(Visit.builder().visitedAt(LocalDate.now()).pin(pin).travel(travel).build());
        VisitImage visitImage1 = visitImageRepository.save(VisitImage.builder().imageUrl("Sample Visit Image1")
                .visit(visit).build());
        VisitImage visitImage2 = visitImageRepository.save(VisitImage.builder().imageUrl("Sample Visit Image2")
                .visit(visit).build());

        // when
        visitImageRepository.deleteByVisitId(visit.getId());

        // then
        assertAll(
                () -> assertThat(visitImageRepository.findById(visitImage1.getId())).isEmpty(),
                () -> assertThat(visitImageRepository.findById(visitImage2.getId())).isEmpty()
        );
    }
}
