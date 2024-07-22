package com.staccato.travel.service;

import org.springframework.stereotype.Service;

import com.staccato.travel.repository.TravelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelService {
    private final TravelRepository travelRepository;
}
