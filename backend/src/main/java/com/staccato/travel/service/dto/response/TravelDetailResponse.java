package com.staccato.travel.service.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.service.dto.response.MemberResponse;
import com.staccato.travel.domain.Travel;
import com.staccato.visit.service.dto.response.VisitResponse;

public record TravelDetailResponse(
        Long travelId,
        @JsonInclude(JsonInclude.Include.NON_NULL) String travelThumbnail,
        String travelTitle,
        @JsonInclude(JsonInclude.Include.NON_NULL) String description,
        LocalDate startAt,
        LocalDate endAt,
        List<MemberResponse> mates,
        List<VisitResponse> visits
) {
    public TravelDetailResponse(Travel travel, List<VisitResponse> visitResponses) {
        this(
                travel.getId(),
                travel.getThumbnailUrl(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartAt(),
                travel.getEndAt(),
                toMemberResponses(travel),
                visitResponses
        );
    }

    private static List<MemberResponse> toMemberResponses(Travel travel) {
        return travel.getMates().stream().map(MemberResponse::new).toList();
    }
}
