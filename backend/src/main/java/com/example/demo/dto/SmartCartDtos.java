package com.example.demo.dto;

import com.example.demo.entity.CustomerInteraction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class SmartCartDtos {
    private SmartCartDtos() {
    }

    public record PromotionIngestionRequest(
            String supermarketCode,
            String supermarketName,
            String canonicalSku,
            String externalSku,
            String name,
            String brand,
            String category,
            String subCategory,
            String productType,
            String imageUrl,
            BigDecimal originalPrice,
            BigDecimal promoPrice,
            Integer shelfLifeDays,
            LocalDateTime promoStart,
            LocalDateTime promoEnd
    ) {
    }

    public record DealCard(
            Long promotionId,
            Long productId,
            String canonicalSku,
            String productName,
            String brand,
            String category,
            String subCategory,
            String productType,
            String imageUrl,
            String supermarket,
            BigDecimal originalPrice,
            BigDecimal promoPrice,
            BigDecimal savings,
            Integer shelfLifeDays,
            LocalDateTime promoEnd
    ) {
    }

    public record ProductDeal(
            DealCard winner,
            List<DealCard> alternatives
    ) {
    }

    public record HeroRecommendation(
            DealCard recommended,
            String affinitySubCategory,
            String fallbackMessage
    ) {
    }

    public record InteractionRequest(Long productId, String subCategory, CustomerInteraction.EventType eventType) {
    }
}
