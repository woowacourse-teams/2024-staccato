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
import com.staccato.s3.service.CloudStorageService;
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
import com.staccato.visit.repository.VisitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {
    private final TravelRepository travelRepository;
    private final TravelMemberRepository travelMemberRepository;
    private final VisitRepository visitRepository;
    private final CloudStorageService cloudStorageService;

    @Transactional
    public TravelIdResponse createTravel(TravelRequest travelRequest, MultipartFile thumbnailFile, Member member) {
        Travel travel = travelRequest.toTravel();
        String thumbnailUrl = uploadFile(thumbnailFile);
        travel.assignThumbnail(thumbnailUrl);
        travel.addTravelMember(member);
        travelRepository.save(travel);
        return new TravelIdResponse(travel.getId());
    }

    private String uploadFile(MultipartFile thumbnailFile) {
        if (thumbnailFile == null) {
            return null;
        }
        String thumbnailUrl = cloudStorageService.uploadFile(thumbnailFile);

        return thumbnailUrl;
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
            String thumbnailUrl = uploadFile(thumbnailFile);
            updatedTravel.assignThumbnail(thumbnailUrl);
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
