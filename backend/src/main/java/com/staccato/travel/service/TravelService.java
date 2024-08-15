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
import com.staccato.travel.service.dto.request.MemoryRequest;
import com.staccato.travel.service.dto.response.MemoryDetailResponse;
import com.staccato.travel.service.dto.response.MemoryIdResponse;
import com.staccato.travel.service.dto.response.MemoryResponses;
import com.staccato.travel.service.dto.response.MomentResponse;
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
    public MemoryIdResponse createTravel(MemoryRequest memoryRequest, MultipartFile thumbnailFile, Member member) {
        Travel travel = memoryRequest.toTravel();
        String thumbnailUrl = uploadFile(thumbnailFile);
        travel.assignThumbnail(thumbnailUrl);
        travel.addTravelMember(member);
        travelRepository.save(travel);
        return new MemoryIdResponse(travel.getId());
    }

    public MemoryResponses readAllTravels(Member member, Integer year) {
        return Optional.ofNullable(year)
                .map(y -> readAllByYear(member, y))
                .orElseGet(() -> readAll(member));
    }

    private MemoryResponses readAllByYear(Member member, Integer year) {
        List<TravelMember> travelMembers = travelMemberRepository.findAllByMemberIdAndStartAtYearDesc(member.getId(), year);
        return getTravelResponses(travelMembers);
    }

    private MemoryResponses readAll(Member member) {
        List<TravelMember> travelMembers = travelMemberRepository.findAllByMemberIdOrderByTravelStartAtDesc(member.getId());
        return getTravelResponses(travelMembers);
    }

    private MemoryResponses getTravelResponses(List<TravelMember> travelMembers) {
        List<Travel> travels = travelMembers.stream()
                .map(TravelMember::getTravel)
                .toList();
        return MemoryResponses.from(travels);
    }

    public MemoryDetailResponse readTravelById(long travelId, Member member) {
        Travel travel = getTravelById(travelId);
        validateOwner(travel, member);
        List<MomentResponse> momentRespons = getVisitResponses(visitRepository.findAllByTravelIdOrderByVisitedAt(travelId));
        return new MemoryDetailResponse(travel, momentRespons);
    }

    private List<MomentResponse> getVisitResponses(List<Visit> visits) {
        return visits.stream()
                .map(visit -> new MomentResponse(visit, getVisitThumbnail(visit)))
                .toList();
    }

    private String getVisitThumbnail(Visit visit) {
        if (visit.hasImage()) {
            return visit.getThumbnailUrl();
        }
        return null;
    }

    @Transactional
    public void updateTravel(MemoryRequest memoryRequest, Long travelId, MultipartFile thumbnailFile, Member member) {
        Travel updatedTravel = memoryRequest.toTravel();
        Travel originTravel = getTravelById(travelId);
        validateOwner(originTravel, member);
        if (!Objects.isNull(thumbnailFile)) {
            String thumbnailUrl = uploadFile(thumbnailFile);
            updatedTravel.assignThumbnail(thumbnailUrl);
        }
        List<Visit> visits = visitRepository.findAllByTravelIdOrderByVisitedAt(travelId);
        originTravel.update(updatedTravel, visits);
    }

    private String uploadFile(MultipartFile thumbnailFile) {
        if (Objects.isNull(thumbnailFile)) {
            return null;
        }
        String thumbnailUrl = cloudStorageService.uploadFile(thumbnailFile);

        return thumbnailUrl;
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
