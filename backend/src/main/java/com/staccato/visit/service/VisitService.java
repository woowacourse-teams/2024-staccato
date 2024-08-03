package com.staccato.visit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.exception.StaccatoException;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImage;
import com.staccato.visit.repository.VisitImageRepository;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;
import com.staccato.visit.service.dto.response.VisitIdResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final TravelRepository travelRepository;
    private final VisitImageRepository visitImageRepository;

    @Transactional
    public VisitIdResponse createVisit(VisitRequest visitRequest) {
        Travel travel = getTravelById(visitRequest.travelId());
        Visit visit = visitRepository.save(visitRequest.toVisit(travel));

        saveVisitImages(visitRequest.visitImagesUrl(), visit);

        return new VisitIdResponse(visit.getId());
    }

    private void saveVisitImages(List<String> visitImagesUrl, Visit visit) {
        if (visitImagesUrl == null) {
            return;
        }
        List<VisitImage> visitImages = visitImagesUrl.stream()
                .map(visitImageUrl -> VisitImage.builder()
                        .imageUrl(visitImageUrl)
                        .visit(visit)
                        .build())
                .toList();

        visitImageRepository.saveAll(visitImages);
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }

    public VisitDetailResponse readVisitById(long visitId) {
        Visit visit = getVisitById(visitId);
        return new VisitDetailResponse(
                visit,
                visit.getVisitImages(),
                visit.getVisitLogs()
        );
    }

    private Visit getVisitById(long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new StaccatoException("요청하신 방문 기록을 찾을 수 없어요."));
    }

    @Transactional
    public void deleteVisitById(Long visitId) {
        visitRepository.deleteById(visitId);
    }
}
