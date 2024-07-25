package com.staccato.visit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.exception.StaccatoException;
import com.staccato.pin.domain.Pin;
import com.staccato.pin.repository.PinRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImage;
import com.staccato.visit.repository.VisitImageRepository;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final PinRepository pinRepository;
    private final TravelRepository travelRepository;
    private final VisitImageRepository visitImageRepository;

    @Transactional
    public long createVisit(VisitRequest visitRequest) {
        Pin pin = getPinById(visitRequest.pinId());
        Travel travel = getTravelById(visitRequest.travelId());
        Visit visit = visitRepository.save(visitRequest.toVisit(pin, travel));

        List<VisitImage> visitImages = makeVisitImages(visitRequest.visitImages(), visit);
        visitImageRepository.saveAll(visitImages);

        return visit.getId();
    }

    private Pin getPinById(long pinId) {
        return pinRepository.findById(pinId)
                .orElseThrow(() -> new StaccatoException("요청하신 핀을 찾을 수 없어요."));
    }

    private Travel getTravelById(long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new StaccatoException("요청하신 여행을 찾을 수 없어요."));
    }

    private List<VisitImage> makeVisitImages(List<String> visitedImages, Visit visit) {
        return visitedImages.stream()
                .map(visitImage -> VisitImage.builder()
                        .imageUrl(visitImage)
                        .visit(visit)
                        .build())
                .toList();
    }

    public VisitDetailResponse readVisitById(long visitId) {
        Visit visit = getVisitById(visitId);
        Pin pin = visit.getPin();
        long visitedCountBefore = visitRepository.countByPinIdAndIsDeletedIsFalseAndVisitedAtBefore(pin.getId(), visit.getVisitedAt());
        return new VisitDetailResponse(
                visit,
                visit.getVisitImages(),
                visitedCountBefore + 1,
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
