package com.staccato.visit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.exception.StaccatoException;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImages;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.request.VisitUpdateRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final TravelRepository travelRepository;

    @Transactional
    public long createVisit(VisitRequest visitRequest) {
        Travel travel = getTravelById(visitRequest.travelId());
        Visit visit = visitRequest.toVisit(travel);
        VisitImages visitImages = new VisitImages(visitRequest.visitImages());
        visit.addVisitImages(visitImages);

        visitRepository.save(visit);

        return visit.getId();
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }

    public VisitDetailResponse readVisitById(long visitId) {
        Visit visit = getVisitById(visitId);
        return new VisitDetailResponse(visit);
    }

    private Visit getVisitById(long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new StaccatoException("요청하신 방문 기록을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteVisitById(Long visitId) {
        visitRepository.deleteById(visitId);
    }

    @Transactional
    public void updateVisitById(long visitId, VisitUpdateRequest visitUpdateRequest, List<MultipartFile> images) {
        Visit visit = getVisitById(visitId);
        List<String> addedImages = List.of(images.get(0).getName()); // 새롭게 추가된 이미지 파일의 url을 가지고 오는 임시 로직
        VisitImages visitImages = VisitImages.builder()
                .existingImages(visitUpdateRequest.visitImagesUrl())
                .addedImages(addedImages)
                .build();

        visit.update(visitUpdateRequest.placeName(), visitImages);
    }
}
