package com.example.demo.controller;

import com.example.demo.dto.SmartCartDtos.ProductDeal;
import com.example.demo.dto.SmartCartDtos.PromotionIngestionRequest;
import com.example.demo.service.DealEvaluationService;
import com.example.demo.service.PromotionIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final DealEvaluationService dealEvaluationService;
    private final PromotionIngestionService ingestionService;

    @GetMapping("/best")
    public ResponseEntity<List<ProductDeal>> bestDeals() {
        return ResponseEntity.ok(dealEvaluationService.bestDeals());
    }

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Integer>> ingest(@RequestBody List<PromotionIngestionRequest> payloads) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("accepted", ingestionService.ingest(payloads)));
    }

    @PostMapping("/ingest/demo")
    public ResponseEntity<Map<String, Integer>> ingestDemo() {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("accepted", ingestionService.ingestDemoPayload()));
    }
}
