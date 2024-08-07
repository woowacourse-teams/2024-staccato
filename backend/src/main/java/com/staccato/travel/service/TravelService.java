package com.staccato.travel.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.domain.TravelMember;
import com.staccato.travel.repository.TravelMemberRepository;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.travel.service.dto.request.TravelRequest;
import com.staccato.travel.service.dto.response.TravelDetailResponse;
import com.staccato.travel.service.dto.response.TravelIdResponse;
import com.staccato.travel.service.dto.response.TravelResponses;
import com.staccato.travel.service.dto.response.VisitResponse;
import com.staccato.visit.domain.Visit;
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

    @Transactional
    public TravelIdResponse createTravel(TravelRequest travelRequest, MultipartFile thumbnailFile, Member member) {
        Travel travel = travelRequest.toTravel();
        String thumbnailUrl = travelRequest.travelThumbnail(); //썸네일 url을 가져오는 임시 로직
        travel.assignThumbnail(thumbnailUrl);
        travel.addTravelMember(member);
        travelRepository.save(travel);
        return new TravelIdResponse(travel.getId());
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

    public TravelDetailResponse readTravelById(long travelId, Member member) {
        Travel travel = getTravelById(travelId);
        validateOwner(travel, member);
        List<VisitResponse> visitResponses = getVisitResponses(visitRepository.findAllByTravelIdOrderByVisitedAt(travelId));
        return new TravelDetailResponse(travel, visitResponses);
    }

    private List<VisitResponse> getVisitResponses(List<Visit> visits) {
        return visits.stream()
                .map(visit -> new VisitResponse(visit, getVisitThumbnail(visit)))
                .toList();
    }

    private String getVisitThumbnail(Visit visit) {
        if (visit.hasImage()) {
            return visit.getThumbnailUrl();
        }
        return null;
    }

    @Transactional
    public void updateTravel(TravelRequest travelRequest, Long travelId, MultipartFile thumbnailFile, Member member) {
        Travel updatedTravel = travelRequest.toTravel();
        Travel originTravel = getTravelById(travelId);
        validateOwner(originTravel, member);
        if (!Objects.isNull(thumbnailFile)) {
            // thumbnailFile을 Url로 변환해 가져오는 로직 추가 예정
            updatedTravel.assignThumbnail(thumbnailFile.getName()); // 새롭게 추가된 이미지 파일의 url을 가지고 오는 임시 로직
        }
        List<Visit> visits = visitRepository.findAllByTravelIdOrderByVisitedAt(travelId);
        originTravel.update(updatedTravel, visits);
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteTravel(long travelId, Member member) {
        travelRepository.findById(travelId).ifPresent(travel -> {
            validateOwner(travel, member);
            validateVisitExistsByTravel(travel);
            travelRepository.deleteById(travelId);
        });
    }

    private void validateOwner(Travel travel, Member member) {
        if (travel.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }

    private void validateVisitExistsByTravel(Travel travel) {
        if (visitRepository.existsByTravel(travel)) {
            throw new StaccatoException("해당 여행 상세에 방문 기록이 남아있어 삭제할 수 없습니다.");
        }
    }
}
