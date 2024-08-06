package com.staccato.travel.service.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberResponse;
import com.staccato.travel.domain.Travel;

public record TravelResponse(
        Long travelId,
        @JsonInclude(JsonInclude.Include.NON_NULL) String travelThumbnail,
        String travelTitle,
        @JsonInclude(JsonInclude.Include.NON_NULL) String description,
        LocalDate startAt,
        LocalDate endAt,
        List<MemberResponse> mates
) {
    public TravelResponse(Travel travel) {
        this(
                travel.getId(),
                travel.getThumbnailUrl(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartAt(),
                travel.getEndAt(),
                createMemberResponses(travel.getMates())
        );
    }

    private static List<MemberResponse> createMemberResponses(List<Member> members) {
        return members.stream()
                .map(MemberResponse::new)
                .toList();
    }
}
