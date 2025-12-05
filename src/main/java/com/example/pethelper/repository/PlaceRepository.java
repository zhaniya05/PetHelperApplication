package com.example.pethelper.repository;

import com.example.pethelper.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("""
    SELECT p FROM Place p
    WHERE (6371 * acos(
        cos(radians(:lat)) * cos(radians(p.latitude)) *
        cos(radians(p.longitude) - radians(:lng)) +
        sin(radians(:lat)) * sin(radians(p.latitude))
    )) <= :radius
    """)
    List<Place> findNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );

}