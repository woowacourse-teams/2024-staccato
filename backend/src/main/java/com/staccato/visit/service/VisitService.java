package com.staccato.visit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.s3.service.CloudStorageService;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImages;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.request.MomentRequest;
import com.staccato.visit.service.dto.request.MomentUpdateRequest;
import com.staccato.visit.service.dto.response.MomentDetailResponse;
import com.staccato.visit.service.dto.response.MomentIdResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final TravelRepository travelRepository;
    private final CloudStorageService cloudStorageService;

    @Transactional
    public MomentIdResponse createVisit(MomentRequest momentRequest, List<MultipartFile> visitImageFiles, Member member) {
        Travel travel = getTravelById(momentRequest.memoryId());
        validateOwner(travel, member);
        Visit visit = momentRequest.toVisit(travel);
        List<String> visitImageUrls = cloudStorageService.uploadFiles(visitImageFiles);
        VisitImages visitImages = new VisitImages(visitImageUrls);
        visit.addVisitImages(visitImages);

        visitRepository.save(visit);

        return new MomentIdResponse(visit.getId());
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }

    public MomentDetailResponse readVisitById(long visitId, Member member) {
        Visit visit = getVisitById(visitId);
        validateOwner(visit.getTravel(), member);
        return new MomentDetailResponse(visit);
    }

    @Transactional
    public void updateVisitById(
            long visitId,
            MomentUpdateRequest momentUpdateRequest,
            List<MultipartFile> visitImageFiles,
            Member member
    ) {
        Visit visit = getVisitById(visitId);
        validateOwner(visit.getTravel(), member);
        List<String> addedImageUrls = cloudStorageService.uploadFiles(visitImageFiles);
        VisitImages visitImages = VisitImages.builder()
                .existingImages(momentUpdateRequest.momentImageUrls())
                .addedImages(addedImageUrls)
                .build();

        visit.update(momentUpdateRequest.placeName(), visitImages);
    }

    private Visit getVisitById(long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new StaccatoException("요청하신 방문 기록을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteVisitById(long visitId, Member member) {
        visitRepository.findById(visitId).ifPresent(visit -> {
            validateOwner(visit.getTravel(), member);
            visitRepository.deleteById(visitId);
        });
    }

    private void validateOwner(Travel travel, Member member) {
        if (travel.isNotOwnedBy(member)) {
            throw new ForbiddenException();
        }
    }
}
