package com.staccato.travel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.domain.TravelMember;
import com.staccato.travel.repository.TravelMemberRepository;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelResponses;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.repository.VisitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {
    private final TravelRepository travelRepository;
    private final TravelMemberRepository travelMemberRepository;
    private final VisitRepository visitRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public long createTravel(TravelRequest travelRequest, Long memberId) {
        Travel travel = travelRepository.save(travelRequest.toTravel());
        saveTravelMember(memberId, travel);
        return travel.getId();
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
        return memberRepository.findByIdAndIsDeletedIsFalse(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Operation"));
    }

    @Transactional
    public void updateTravel(TravelRequest travelRequest, Long travelId) {
        Travel updatedTravel = travelRequest.toTravel();
        Travel originTravel = getTravelById(travelId);
        List<Visit> visits = visitRepository.findAllByTravelId(travelId);
        originTravel.update(updatedTravel, visits);
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }

    public TravelResponses readAllTravels(long memberId, Integer year) {
        return Optional.ofNullable(year)
                .map(y -> readAllByYear(memberId, y))
                .orElseGet(() -> readAll(memberId));
    }

    private TravelResponses readAll(long memberId) {
        List<TravelMember> travelMembers = travelMemberRepository.findAllByMemberIdAndIsDeletedIsFalse(memberId);
        return getTravelResponses(travelMembers);
    }

    private TravelResponses readAllByYear(long memberId, Integer year) {
        List<TravelMember> travelMembers = travelMemberRepository.findAllByMemberIdAndTravelStartAtYear(memberId, year);
        return getTravelResponses(travelMembers);
    }

    private TravelResponses getTravelResponses(List<TravelMember> travelMembers) {
        List<Travel> travels = travelMembers.stream()
                .map(TravelMember::getTravel)
                .toList();
        return TravelResponses.from(travels);
    }
}
