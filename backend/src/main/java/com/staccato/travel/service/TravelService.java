package com.staccato.travel.service;

import org.springframework.stereotype.Service;

import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Mate;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.MateRepository;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelService {
    private final TravelRepository travelRepository;
    private final MateRepository mateRepository;
    private final MemberRepository memberRepository;

    public TravelResponse createTravel(TravelRequest travelRequest, Long memberId) {
        Travel travel = Travel.builder()
                .title(travelRequest.travelTitle())
                .description(travelRequest.description())
                .startAt(travelRequest.startAt())
                .endAt(travelRequest.endAt())
                .build();
        Travel savedTravel = travelRepository.save(travel);
        saveMate(memberId, savedTravel);
        return new TravelResponse(travel);
    }

    private void saveMate(Long memberId, Travel savedTravel) {
        Member member = getMemberById(memberId);
        Mate mate = Mate.builder()
                .travel(savedTravel)
                .member(member)
                .build();
        mateRepository.save(mate);
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Operation"));
    }
}
