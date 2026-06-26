package com.example.demo.controller;

import com.example.demo.dto.SmartCartDtos.InteractionRequest;
import com.example.demo.entity.CustomerInteraction;
import com.example.demo.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping("/interactions")
    public ResponseEntity<Map<String, Object>> log(Authentication authentication, @RequestBody InteractionRequest request) {
        CustomerInteraction interaction = telemetryService.log(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", interaction.getId(),
                "subCategory", interaction.getSubCategory(),
                "eventType", interaction.getEventType()
        ));
    }
}
