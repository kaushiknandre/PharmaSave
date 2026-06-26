package com.example.demo.service;

import com.example.demo.dto.SmartCartDtos.PromotionIngestionRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.Promotion;
import com.example.demo.entity.Supermarket;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.PromotionRepository;
import com.example.demo.repository.SupermarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionIngestionService {

    private final PromotionWindowService promotionWindowService;
    private final ProductRepository productRepository;
    private final SupermarketRepository supermarketRepository;
    private final PromotionRepository promotionRepository;

    @Scheduled(cron = "0 5 0 * * THU")
    public void scheduledWeeklyIngestion() {
        ingestDemoPayload();
    }

    public int ingest(List<PromotionIngestionRequest> payloads) {
        int accepted = 0;
        for (PromotionIngestionRequest payload : payloads) {
            if (!promotionWindowService.isStrictWeeklyCycle(payload.promoStart(), payload.promoEnd())) {
                continue;
            }
            if (promotionRepository.existsByExternalSkuAndPromoStartAndSupermarket_Code(
                    payload.externalSku(),
                    payload.promoStart(),
                    payload.supermarketCode()
            )) {
                continue;
            }

            Supermarket supermarket = supermarketRepository.findByCode(payload.supermarketCode())
                    .orElseGet(() -> supermarketRepository.save(Supermarket.builder()
                            .code(payload.supermarketCode())
                            .name(payload.supermarketName())
                            .build()));

            Product product = productRepository.findByCanonicalSku(payload.canonicalSku())
                    .orElseGet(() -> productRepository.save(Product.builder()
                            .canonicalSku(payload.canonicalSku())
                            .name(payload.name())
                            .brand(payload.brand())
                            .category(payload.category())
                            .subCategory(payload.subCategory())
                            .productType(Product.ProductType.valueOf(payload.productType()))
                            .imageUrl(payload.imageUrl())
                            .isActive(true)
                            .build()));

            promotionRepository.save(Promotion.builder()
                    .supermarket(supermarket)
                    .product(product)
                    .externalSku(payload.externalSku())
                    .originalPrice(payload.originalPrice())
                    .promoPrice(payload.promoPrice())
                    .shelfLifeDays(payload.shelfLifeDays())
                    .promoStart(payload.promoStart())
                    .promoEnd(payload.promoEnd())
                    .ingestedAt(LocalDateTime.now())
                    .build());
            accepted++;
        }
        return accepted;
    }

    public int ingestDemoPayload() {
        PromotionWindowService.PromotionCycle cycle = promotionWindowService.currentCycle();
        return ingest(List.of(
                payload("CAREPLUS", "CarePlus Market", "PAIN-PARA-500", "CP-PARA-500", "Paracetamol 500mg Caplets", "MediCore", "OTC Medicine", "Pain Relief", "OTC_MEDICINE", "#dceefb", "8.99", "5.49", 420, cycle),
                payload("HEALTHMART", "HealthMart", "PAIN-PARA-500", "HM-PARA-500", "Paracetamol 500mg Caplets", "MediCore", "OTC Medicine", "Pain Relief", "OTC_MEDICINE", "#dceefb", "8.79", "5.49", 510, cycle),
                payload("FRESHFAIR", "FreshFair", "PAIN-PARA-500", "FF-PARA-500", "Paracetamol 500mg Caplets", "MediCore", "OTC Medicine", "Pain Relief", "OTC_MEDICINE", "#dceefb", "8.49", "5.79", 360, cycle),
                payload("CAREPLUS", "CarePlus Market", "SKIN-ALOE-GEL", "CP-ALOE-GEL", "Aloe Recovery Gel", "GlowLab", "Cosmetics", "Skincare", "COSMETIC", "#e1f7e8", "14.99", "9.99", 720, cycle),
                payload("HEALTHMART", "HealthMart", "SKIN-ALOE-GEL", "HM-ALOE-GEL", "Aloe Recovery Gel", "GlowLab", "Cosmetics", "Skincare", "COSMETIC", "#e1f7e8", "13.99", "9.49", 690, cycle),
                payload("FRESHFAIR", "FreshFair", "SKIN-ALOE-GEL", "FF-ALOE-GEL", "Aloe Recovery Gel", "GlowLab", "Cosmetics", "Skincare", "COSMETIC", "#e1f7e8", "15.49", "9.49", 760, cycle),
                payload("CAREPLUS", "CarePlus Market", "COLD-VITC-1000", "CP-VITC-1000", "Vitamin C 1000mg Effervescent", "NutraNest", "OTC Medicine", "Cold & Flu", "OTC_MEDICINE", "#fff4ce", "11.99", "7.99", 300, cycle),
                payload("HEALTHMART", "HealthMart", "COLD-VITC-1000", "HM-VITC-1000", "Vitamin C 1000mg Effervescent", "NutraNest", "OTC Medicine", "Cold & Flu", "OTC_MEDICINE", "#fff4ce", "10.99", "7.49", 280, cycle),
                payload("FRESHFAIR", "FreshFair", "COLD-VITC-1000", "FF-VITC-1000", "Vitamin C 1000mg Effervescent", "NutraNest", "OTC Medicine", "Cold & Flu", "OTC_MEDICINE", "#fff4ce", "12.49", "7.49", 410, cycle)
        ));
    }

    private PromotionIngestionRequest payload(
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
            String originalPrice,
            String promoPrice,
            int shelfLifeDays,
            PromotionWindowService.PromotionCycle cycle
    ) {
        return new PromotionIngestionRequest(
                supermarketCode,
                supermarketName,
                canonicalSku,
                externalSku,
                name,
                brand,
                category,
                subCategory,
                productType,
                imageUrl,
                new BigDecimal(originalPrice),
                new BigDecimal(promoPrice),
                shelfLifeDays,
                cycle.start(),
                cycle.end()
        );
    }
}
