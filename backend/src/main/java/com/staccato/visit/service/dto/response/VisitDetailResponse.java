package com.staccato.visit.service.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImage;
import com.staccato.visit.domain.VisitLog;

public record VisitDetailResponse(
        Long visitId,
        String placeName,
        List<String> visitImages,
        String address,
        LocalDate visitedAt,
        Long visitedCount,
        List<VisitLogResponse> visitLogs
) {
    public VisitDetailResponse(Visit visit, List<VisitImage> visitImages, Long visitedCount, List<VisitLog> visitLogs) {
        this(
                visit.getId(),
                visit.getPin().getPlace(),
                visitImages.stream().map(VisitImage::getImageUrl).toList(),
                visit.getPin().getAddress(),
                visit.getVisitedAt(),
                visitedCount,
                visitLogs.stream().map(VisitLogResponse::new).toList()
        );
    }
}
