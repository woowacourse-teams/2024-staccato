package com.staccato.travel.service;

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

    private TravelMember saveTravelMember(Long memberId, Travel travel) {
        Member member = getMemberById(memberId);
        TravelMember mate = TravelMember.builder()
                .travel(travel)
                .member(member)
                .build();
        return travelMemberRepository.save(mate);
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Operation"));
    }

    @Transactional
    public void updateTravel(TravelRequest travelRequest, Long travelId) {
        Travel updatedTravel = travelRequest.toTravel();
        Travel originTravel = getTravelById(travelId);
        originTravel.update(updatedTravel);
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }
}
