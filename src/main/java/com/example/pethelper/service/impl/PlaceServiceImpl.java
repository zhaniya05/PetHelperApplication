package com.example.pethelper.service.impl;

import com.example.pethelper.entity.Place;
import com.example.pethelper.repository.PlaceRepository;
import com.example.pethelper.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public List<Place> getAll() {
        return placeRepository.findAll();
    }

    @Override
    public List<Place> findNearby(double lat, double lng, double radiusKm) {
        return placeRepository.findNearby(lat, lng, radiusKm);
    }

    @Override
    public Place save(Place place) {
        return placeRepository.save(place);
    }
}

