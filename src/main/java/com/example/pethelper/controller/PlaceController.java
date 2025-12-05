package com.example.pethelper.controller;

import com.example.pethelper.entity.Place;
import com.example.pethelper.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public String placesPage(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(defaultValue = "5") double radius,
            Model model
    ) {
        List<Place> places;
        if (lat != null && lng != null) {
            places = placeService.findNearby(lat, lng, radius);
        } else {
            places = placeService.getAll();
        }

        model.addAttribute("places", places);
        model.addAttribute("newPlace", new Place()); // for form
        return "places";
    }

    @PostMapping("/save")
    public String savePlace(@ModelAttribute Place place) {
        placeService.save(place);
        return "redirect:/places";
    }
}
