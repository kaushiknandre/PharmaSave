package com.example.demo.service;

import com.example.demo.dto.SmartCartDtos.HeroRecommendation;
import com.example.demo.dto.SmartCartDtos.ProductDeal;
import com.example.demo.entity.CustomerInteraction;
import com.example.demo.entity.User;
import com.example.demo.repository.CustomerInteractionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalizationService {

    private final UserRepository userRepository;
    private final CustomerInteractionRepository interactionRepository;
    private final DealEvaluationService dealEvaluationService;

    public HeroRecommendation heroFor(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<ProductDeal> deals = dealEvaluationService.bestDeals();
        if (deals.isEmpty()) {
            return new HeroRecommendation(null, null, "No active Thursday-Wednesday promotions are available right now.");
        }

        String topSubCategory = topSubCategory(user.getId());
        ProductDeal selected = deals.stream()
                .filter(deal -> topSubCategory != null && deal.winner().subCategory().equalsIgnoreCase(topSubCategory))
                .findFirst()
                .orElse(deals.get(0));

        String fallback = topSubCategory == null
                ? "Recommendation uses the current best-value deal until you view or click more categories."
                : null;
        return new HeroRecommendation(selected.winner(), topSubCategory, fallback);
    }

    private String topSubCategory(Long userId) {
        Map<String, Integer> scores = new HashMap<>();
        for (CustomerInteraction interaction : interactionRepository.findByUserId(userId)) {
            scores.merge(interaction.getSubCategory(), weight(interaction.getEventType()), Integer::sum);
        }
        return scores.entrySet().stream()
                .max(Comparator.comparing(Map.Entry<String, Integer>::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private int weight(CustomerInteraction.EventType eventType) {
        return switch (eventType) {
            case CLICK -> 3;
            case CATEGORY -> 2;
            case VIEW -> 1;
        };
    }
}
