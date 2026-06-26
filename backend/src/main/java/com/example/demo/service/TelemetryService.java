package com.example.demo.service;

import com.example.demo.dto.SmartCartDtos.InteractionRequest;
import com.example.demo.entity.CustomerInteraction;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CustomerInteractionRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TelemetryService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CustomerInteractionRepository interactionRepository;

    public CustomerInteraction log(String username, InteractionRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = request.productId() == null
                ? null
                : productRepository.findById(request.productId()).orElse(null);

        String subCategory = request.subCategory();
        if ((subCategory == null || subCategory.isBlank()) && product != null) {
            subCategory = product.getSubCategory();
        }
        if (subCategory == null || subCategory.isBlank()) {
            throw new IllegalArgumentException("subCategory is required when productId is not supplied");
        }

        return interactionRepository.save(CustomerInteraction.builder()
                .user(user)
                .product(product)
                .subCategory(subCategory)
                .eventType(request.eventType())
                .occurredAt(LocalDateTime.now())
                .build());
    }
}
