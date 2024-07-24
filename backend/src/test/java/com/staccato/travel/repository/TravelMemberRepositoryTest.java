package com.staccato.travel.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.domain.TravelMember;

@DataJpaTest
class TravelMemberRepositoryTest {
    @Autowired
    private TravelMemberRepository travelMemberRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TravelRepository travelRepository;

    @DisplayName("사용자 식별자와 년도로 여행 상세 목록을 조회한다.")
    @Test
    void findAllByMemberIdAndTravelStartAtYear() {
        // given
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());
        Travel travel = travelRepository.save(createTravel(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)));
        Travel travel2 = travelRepository.save(createTravel(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Travel travel3 = travelRepository.save(createTravel(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10)));
        travelMemberRepository.save(new TravelMember(member, travel));
        travelMemberRepository.save(new TravelMember(member, travel2));
        travelMemberRepository.save(new TravelMember(member, travel3));

        // when
        List<TravelMember> result = travelMemberRepository.findAllByMemberIdAndTravelStartAtYear(member.getId(), 2023);

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("사용자 식별자와 년도로 삭제 되지 않은 여행 상세 목록만 조회한다.")
    @Test
    void findAllByMemberIdAndTravelStartAtYearWithoutDeleted() {
        // given
        Member member = memberRepository.save(Member.builder().nickname("staccato").build());
        Travel travel = travelRepository.save(createTravel(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 10)));
        Travel travel2 = travelRepository.save(createTravel(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        TravelMember target = travelMemberRepository.save(new TravelMember(member, travel));
        travelMemberRepository.save(new TravelMember(member, travel2));
        travelMemberRepository.deleteById(target.getId());

        // when
        List<TravelMember> result = travelMemberRepository.findAllByMemberIdAndTravelStartAtYear(member.getId(), 2023);

        // then
        assertThat(result).hasSize(1);
    }

    private static Travel createTravel(LocalDate startAt, LocalDate endAt) {
        return Travel.builder()
                .title("여행")
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
