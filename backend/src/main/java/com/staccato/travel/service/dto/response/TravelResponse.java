package com.staccato.travel.service.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.service.dto.response.MemberResponses;
import com.staccato.travel.domain.Travel;

public record TravelResponse(
        Long travelId,
        @JsonInclude(JsonInclude.Include.NON_NULL) String travelThumbnail,
        String travelTitle,
        @JsonInclude(JsonInclude.Include.NON_NULL) String description,
        LocalDate startAt,
        LocalDate endAt,
        MemberResponses mates
) {
    public TravelResponse(Travel travel) {
        this(
                travel.getId(),
                travel.getThumbnailUrl(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartAt(),
                travel.getEndAt(),
                MemberResponses.from(travel.getMates())
        );
    }
}
