package com.staccato.travel.service.dto.response;

import java.time.LocalDate;

import com.staccato.member.service.dto.response.MemberResponses;
import com.staccato.visit.service.dto.response.VisitResponses;

public record TravelDetailResponse(
        Long travelId,
        String travelThumbnail,
        String travelTitle,
        String description,
        LocalDate startAt,
        LocalDate endAt,
        MemberResponses mates,
        VisitResponses visits
) {
}
