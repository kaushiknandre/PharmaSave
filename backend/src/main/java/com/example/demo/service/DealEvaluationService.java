package com.example.demo.service;

import com.example.demo.dto.SmartCartDtos.DealCard;
import com.example.demo.dto.SmartCartDtos.ProductDeal;
import com.example.demo.entity.Promotion;
import com.example.demo.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DealEvaluationService {

    private final PromotionRepository promotionRepository;
    private final PromotionWindowService promotionWindowService;

    public List<ProductDeal> bestDeals() {
        List<Promotion> activePromotions = promotionRepository.findActivePromotions(promotionWindowService.now());
        return evaluate(activePromotions);
    }

    public List<ProductDeal> evaluate(List<Promotion> promotions) {
        Map<String, List<Promotion>> bySku = promotions.stream()
                .collect(Collectors.groupingBy(
                        promotion -> promotion.getProduct().getCanonicalSku(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ProductDeal> deals = new ArrayList<>();
        for (List<Promotion> groupedPromotions : bySku.values()) {
            List<Promotion> sorted = groupedPromotions.stream()
                    .sorted(bestPromotionComparator())
                    .toList();
            DealCard winner = toDealCard(sorted.get(0));
            List<DealCard> alternatives = sorted.stream().skip(1).map(this::toDealCard).toList();
            deals.add(new ProductDeal(winner, alternatives));
        }

        return deals.stream()
                .sorted(Comparator.comparing((ProductDeal deal) -> deal.winner().savings()).reversed())
                .toList();
    }

    private Comparator<Promotion> bestPromotionComparator() {
        return Comparator
                .comparing(Promotion::getPromoPrice)
                .thenComparing(Comparator.comparing(Promotion::getShelfLifeDays).reversed())
                .thenComparing(promotion -> promotion.getSupermarket().getName());
    }

    private DealCard toDealCard(Promotion promotion) {
        BigDecimal savings = promotion.getOriginalPrice().subtract(promotion.getPromoPrice()).max(BigDecimal.ZERO);
        return new DealCard(
                promotion.getId(),
                promotion.getProduct().getId(),
                promotion.getProduct().getCanonicalSku(),
                promotion.getProduct().getName(),
                promotion.getProduct().getBrand(),
                promotion.getProduct().getCategory(),
                promotion.getProduct().getSubCategory(),
                promotion.getProduct().getProductType().name(),
                promotion.getProduct().getImageUrl(),
                promotion.getSupermarket().getName(),
                promotion.getOriginalPrice(),
                promotion.getPromoPrice(),
                savings,
                promotion.getShelfLifeDays(),
                promotion.getPromoEnd()
        );
    }
}
