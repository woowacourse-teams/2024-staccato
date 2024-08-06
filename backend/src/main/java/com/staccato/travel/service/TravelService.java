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
import com.staccato.travel.service.dto.response.TravelDetailResponse;
import com.staccato.travel.service.dto.response.TravelResponses;
import com.staccato.travel.service.dto.response.VisitResponse;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImage;
import com.staccato.visit.repository.VisitImageRepository;
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
    private final VisitImageRepository visitImageRepository;

    @Transactional
    public long createTravel(TravelRequest travelRequest, Member member) {
        Travel travel = travelRepository.save(travelRequest.toTravel());
        saveTravelMember(member, travel);
        return travel.getId();
    }

    private void saveTravelMember(Member member, Travel travel) {
        TravelMember mate = TravelMember.builder()
                .travel(travel)
                .member(member)
                .build();
        travelMemberRepository.save(mate);
    }

    public TravelResponses readAllTravels(Member member, Integer year) {
        return Optional.ofNullable(year)
                .map(y -> readAllByYear(member, y))
                .orElseGet(() -> readAll(member));
    }

    private TravelResponses readAllByYear(Member member, Integer year) {
        List<TravelMember> travelMembers = travelMemberRepository.findAllByMemberIdAndStartAtYearDesc(member.getId(), year);
        return getTravelResponses(travelMembers);
    }

    private TravelResponses readAll(Member member) {
        List<TravelMember> travelMembers = travelMemberRepository.findAllByMemberIdOrderByTravelStartAtDesc(member.getId());
        return getTravelResponses(travelMembers);
    }

    private TravelResponses getTravelResponses(List<TravelMember> travelMembers) {
        List<Travel> travels = travelMembers.stream()
                .map(TravelMember::getTravel)
                .toList();
        return TravelResponses.from(travels);
    }

    @Transactional
    public void updateTravel(TravelRequest travelRequest, Long travelId) {
        Travel updatedTravel = travelRequest.toTravel();
        Travel originTravel = getTravelById(travelId);
        List<Visit> visits = visitRepository.findAllByTravelIdOrderByVisitedAt(travelId);
        originTravel.update(updatedTravel, visits);
    }

    @Transactional
    public void deleteTravel(Long travelId) {
        validateVisitExistsByTravelId(travelId);
        visitRepository.deleteAllByTravelId(travelId);
        travelRepository.deleteById(travelId);
    }

    private void validateVisitExistsByTravelId(Long travelId) {
        if (visitRepository.existsByTravelId(travelId)) {
            throw new StaccatoException("해당 여행 상세에 방문 기록이 남아있어 삭제할 수 없습니다.");
        }
    }

    public TravelDetailResponse readTravelById(long travelId) {
        Travel travel = getTravelById(travelId);
        List<VisitResponse> visitResponses = getVisitResponses(visitRepository.findAllByTravelIdOrderByVisitedAt(travelId));
        return new TravelDetailResponse(travel, visitResponses);
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }

    private List<VisitResponse> getVisitResponses(List<Visit> visits) {
        return visits.stream()
                .map(visit -> new VisitResponse(visit, getFirstVisitImageUrl(visit)))
                .toList();
    }

    private String getFirstVisitImageUrl(Visit visit) {
        return visitImageRepository.findFirstByVisitId(visit.getId())
                .map(VisitImage::getImageUrl)
                .orElse(null);
    }
}
