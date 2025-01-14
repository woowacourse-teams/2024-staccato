package com.staccato.moment.controller;

import java.util.List;

import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.StaccatoRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentIdResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponse;
import com.staccato.moment.service.dto.response.MomentLocationResponses;
import com.staccato.moment.service.dto.response.StaccatoDetailResponse;
import com.staccato.moment.service.dto.response.StaccatoIdResponse;
import com.staccato.moment.service.dto.response.StaccatoLocationResponse;
import com.staccato.moment.service.dto.response.StaccatoLocationResponses;

public class StaccatoDtoMapper {
    public static MomentRequest toMomentRequest(StaccatoRequest staccatoRequest) {
        return new MomentRequest(
                staccatoRequest.staccatoTitle(),
                staccatoRequest.placeName(),
                staccatoRequest.address(),
                staccatoRequest.latitude(),
                staccatoRequest.longitude(),
                staccatoRequest.visitedAt(),
                staccatoRequest.categoryId(),
                staccatoRequest.staccatoImageUrls()
        );
    }

    public static StaccatoIdResponse toStaccatoIdResponse(MomentIdResponse momentIdResponse) {
        return new StaccatoIdResponse(momentIdResponse.momentId());
    }

    public static StaccatoLocationResponse toStaccatoLocationResponse(MomentLocationResponse momentLocationResponse) {
        return new StaccatoLocationResponse(
                momentLocationResponse.momentId(),
                momentLocationResponse.latitude(),
                momentLocationResponse.longitude()
        );
    }

    public static StaccatoLocationResponses toStaccatoLocationResponses(MomentLocationResponses momentLocationResponses) {
        List<StaccatoLocationResponse> staccatoLocationResponses = momentLocationResponses.momentLocationResponses().stream()
                .map(StaccatoDtoMapper::toStaccatoLocationResponse)
                .toList();
        return new StaccatoLocationResponses(staccatoLocationResponses);
    }

    public static StaccatoDetailResponse toStaccatoDetailResponse(MomentDetailResponse momentDetailResponse) {
        return new StaccatoDetailResponse(
                momentDetailResponse.memoryId(),
                momentDetailResponse.momentId(),
                momentDetailResponse.memoryTitle(),
                momentDetailResponse.startAt(),
                momentDetailResponse.endAt(),
                momentDetailResponse.staccatoTitle(),
                momentDetailResponse.momentImageUrls(),
                momentDetailResponse.visitedAt(),
                momentDetailResponse.feeling(),
                momentDetailResponse.placeName(),
                momentDetailResponse.address(),
                momentDetailResponse.latitude(),
                momentDetailResponse.longitude()
        );
    }
}
