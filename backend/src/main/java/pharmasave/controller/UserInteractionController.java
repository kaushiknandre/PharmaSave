package pharmasave.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pharmasave.dto.UserInteractionRequest;
import pharmasave.service.UserInteractionService;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class UserInteractionController {

    private final UserInteractionService interactionService;

    @PostMapping
    public ResponseEntity<String> saveInteraction(
            @RequestBody UserInteractionRequest request) {

        interactionService.saveInteraction(request);

        return ResponseEntity.ok("Interaction saved successfully.");
    }
}