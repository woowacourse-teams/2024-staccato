package com.staccato.travel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.domain.TravelMember;
import com.staccato.travel.repository.TravelMemberRepostiory;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.travel.service.dto.request.TravelRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {
    private final TravelRepository travelRepository;
    private final TravelMemberRepostiory travelMemberRepostiory;
    private final MemberRepository memberRepository;

    @Transactional
    public long createTravel(TravelRequest travelRequest, Long memberId) {
        Travel travel = travelRepository.save(travelRequest.toTravel());
        saveTravelMember(memberId, travel);
        return travel.getId();
    }

    private TravelMember saveTravelMember(Long memberId, Travel travel) {
        Member member = getMemberById(memberId);
        TravelMember mate = TravelMember.builder()
                .travel(travel)
                .member(member)
                .build();
        return travelMemberRepostiory.save(mate);
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Operation"));
    }
}
