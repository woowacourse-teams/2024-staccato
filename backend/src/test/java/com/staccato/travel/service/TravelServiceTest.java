package com.staccato.travel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.domain.TravelMember;
import com.staccato.travel.repository.TravelMemberRepository;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelResponse;

class TravelServiceTest extends ServiceSliceTest {
    @Autowired
    private TravelService travelService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TravelMemberRepository travelMemberRepository;
    @Autowired
    private TravelRepository travelRepository;

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 생성하고 작성자를 저장한다.")
    @Test
    void createTravel() {
        // given
        TravelRequest travelRequest = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());

        // when
        TravelResponse travel = travelService.createTravel(travelRequest, member.getId());
        TravelMember travelMember = travelMemberRepository.findAll().get(0);

        // then
        assertAll(
                () -> assertThat(travelMember.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(travelMember.getTravel().getId()).isEqualTo(travel.travelId())
        );
    }

    @DisplayName("여행 상세 정보를 기반으로, 여행 상세를 수정한다.")
    @Test
    void updateTravel() {
        // given
        Long travelId = 1L;
        TravelRequest originTravel = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());
        travelService.createTravel(originTravel, member.getId());
        TravelRequest updatedTravel = new TravelRequest(
                "https://example.com/travels/geumohrm.jpg",
                "2023 신나는 여름 휴가",
                "친한 친구들과 함께한 여름 휴가 여행",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10)
        );

        // when
        travelService.updateTravel(updatedTravel, travelId);
        Travel travel = travelRepository.findById(travelId).get();

        // then
        assertAll(
                () -> assertThat(travel.getId()).isEqualTo(travelId),
                () -> assertThat(travel.getTitle()).isEqualTo(updatedTravel.travelTitle()),
                () -> assertThat(travel.getDescription()).isEqualTo(updatedTravel.description()),
                () -> assertThat(travel.getStartAt()).isEqualTo(updatedTravel.startAt()),
                () -> assertThat(travel.getEndAt()).isEqualTo(updatedTravel.endAt())
        );
    }
}
