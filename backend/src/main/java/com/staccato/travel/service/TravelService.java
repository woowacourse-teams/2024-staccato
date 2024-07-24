package com.staccato.travel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.domain.TravelMember;
import com.staccato.travel.repository.TravelMemberRepository;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelDetailResponses;
import com.staccato.travel.service.dto.response.TravelResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {
    private final TravelRepository travelRepository;
    private final TravelMemberRepository travelMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TravelResponse createTravel(TravelRequest travelRequest, Long memberId) {
        Travel travel = travelRepository.save(travelRequest.toTravel());
        saveTravelMember(memberId, travel);
        return new TravelResponse(travel);
    }

    private void saveTravelMember(Long memberId, Travel travel) {
        Member member = getMemberById(memberId);
        TravelMember mate = TravelMember.builder()
                .travel(travel)
                .member(member)
                .build();
        travelMemberRepository.save(mate);
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Operation"));
    }

    public TravelDetailResponses readAllTravels(long memberId, Integer year) {
        return Optional.ofNullable(year)
                .map(y -> readAllByYear(memberId, y))
                .orElseGet(() -> readAll(memberId));
    }

    private TravelDetailResponses readAll(long memberId) {
        List<TravelMember> travelMembers = travelMemberRepository.findAllByMemberId(memberId);
        return getTravelDetailResponses(travelMembers);
    }

    private TravelDetailResponses readAllByYear(long memberId, Integer year) {
        List<TravelMember> travelMembers = travelMemberRepository.findAllByMemberIdAndTravelStartAtYear(memberId, year);
        return getTravelDetailResponses(travelMembers);
    }

    private TravelDetailResponses getTravelDetailResponses(List<TravelMember> travelMembers) {
        List<Travel> travels = travelMembers.stream()
                .map(TravelMember::getTravel)
                .toList();
        return TravelDetailResponses.from(travels);
    }
}
