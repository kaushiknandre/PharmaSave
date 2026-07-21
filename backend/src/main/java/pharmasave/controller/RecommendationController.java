package pharmasave.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pharmasave.dto.RecommendationResponse;
import pharmasave.service.RecommendationService;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<RecommendationResponse> getRecommendation() {

        RecommendationResponse response =
                recommendationService.getRecommendation();

        return ResponseEntity.ok(response);
    }
}