package com.staccato.visit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImages;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.request.VisitUpdateRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;
import com.staccato.visit.service.dto.response.VisitIdResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final TravelRepository travelRepository;

    @Transactional
    public VisitIdResponse createVisit(VisitRequest visitRequest, Member member) {
        Travel travel = getTravelById(visitRequest.travelId());
        validateOwner(travel, member);
        Visit visit = visitRequest.toVisit(travel);
        VisitImages visitImages = new VisitImages(visitRequest.visitImageUrls());
        visit.addVisitImages(visitImages);

        visitRepository.save(visit);

        return new VisitIdResponse(visit.getId());
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }

    public VisitDetailResponse readVisitById(long visitId, Member member) {
        Visit visit = getVisitById(visitId);
        validateOwner(visit.getTravel(), member);
        return new VisitDetailResponse(visit);
    }

    @Transactional
    public void updateVisitById(
            long visitId,
            VisitUpdateRequest visitUpdateRequest,
            List<MultipartFile> visitImageFiles,
            Member member
    ) {
        Visit visit = getVisitById(visitId);
        validateOwner(visit.getTravel(), member);
        List<String> addedImages = List.of(visitImageFiles.get(0).getName()); // 새롭게 추가된 이미지 파일의 url을 가지고 오는 임시 로직
        VisitImages visitImages = VisitImages.builder()
                .existingImages(visitUpdateRequest.visitImageUrls())
                .addedImages(addedImages)
                .build();

        visit.update(visitUpdateRequest.placeName(), visitImages);
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
