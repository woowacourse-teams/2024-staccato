package com.staccato.travel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class TravelService {
    private final TravelRepository travelRepository;
    private final MateRepository mateRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TravelResponse createTravel(TravelRequest travelRequest, Long memberId) {
        Travel travel = travelRequest.toTravel();
        Travel savedTravel = travelRepository.save(travel);
        saveMate(memberId, savedTravel);
        return new TravelResponse(savedTravel);
    }

    private void saveMate(Long memberId, Travel travel) {
        Member member = getMemberById(memberId);
        Mate mate = Mate.builder()
                .travel(travel)
                .member(member)
                .build();
        mateRepository.save(mate);
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Operation"));
    }
}
