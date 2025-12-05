package com.example.pethelper.service;

import com.example.pethelper.entity.Place;

import java.util.List;

public interface PlaceService {
    List<Place> getAll();
    List<Place> findNearby(double lat, double lng, double radiusKm);
    Place save(Place place);
}
