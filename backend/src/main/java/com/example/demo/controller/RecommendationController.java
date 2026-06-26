package com.example.demo.controller;

import com.example.demo.dto.SmartCartDtos.HeroRecommendation;
import com.example.demo.service.PersonalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final PersonalizationService personalizationService;

    @GetMapping("/hero")
    public ResponseEntity<HeroRecommendation> hero(Authentication authentication) {
        return ResponseEntity.ok(personalizationService.heroFor(authentication.getName()));
    }
}
