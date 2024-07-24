package com.staccato.travel.service.dto.response;

import java.time.LocalDate;

import com.staccato.member.service.dto.response.MemberResponses;
import com.staccato.travel.domain.Travel;

public record TravelDetailResponse(
        Long travelId,
        String travelThumbnail,
        String travelTitle,
        String description,
        LocalDate startAt,
        LocalDate endAt,
        MemberResponses mates
) {
    public TravelDetailResponse(Travel travel){
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
