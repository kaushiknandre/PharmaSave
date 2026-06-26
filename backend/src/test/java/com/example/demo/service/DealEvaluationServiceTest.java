package com.example.demo.service;

import com.example.demo.dto.SmartCartDtos.ProductDeal;
import com.example.demo.entity.Product;
import com.example.demo.entity.Promotion;
import com.example.demo.entity.Supermarket;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class DealEvaluationServiceTest {

    private final DealEvaluationService service = new DealEvaluationService(null, null);

    @Test
    void selectsLowestPriceThenLongestShelfLifeForMatchingSku() {
        Product product = Product.builder()
                .id(1L)
                .canonicalSku("PAIN-PARA-500")
                .name("Paracetamol 500mg")
                .brand("MediCore")
                .category("OTC Medicine")
                .subCategory("Pain Relief")
                .productType(Product.ProductType.OTC_MEDICINE)
                .imageUrl("#fff")
                .build();

        Promotion carePlus = promotion(product, "CarePlus", "5.49", 420);
        Promotion healthMart = promotion(product, "HealthMart", "5.49", 510);
        Promotion freshFair = promotion(product, "FreshFair", "5.79", 700);

        List<ProductDeal> deals = service.evaluate(List.of(carePlus, healthMart, freshFair));

        assertThat(deals).hasSize(1);
        assertThat(deals.get(0).winner().supermarket()).isEqualTo("HealthMart");
        assertThat(deals.get(0).winner().promoPrice()).isEqualByComparingTo("5.49");
        assertThat(deals.get(0).winner().shelfLifeDays()).isEqualTo(510);
    }

    private Promotion promotion(Product product, String supermarket, String price, int shelfLifeDays) {
        return Promotion.builder()
                .id((long) shelfLifeDays)
                .product(product)
                .supermarket(Supermarket.builder().name(supermarket).code(supermarket.toUpperCase()).build())
                .externalSku(supermarket + "-SKU")
                .originalPrice(new BigDecimal("8.99"))
                .promoPrice(new BigDecimal(price))
                .shelfLifeDays(shelfLifeDays)
                .promoStart(LocalDateTime.of(2026, 6, 25, 0, 0))
                .promoEnd(LocalDateTime.of(2026, 7, 1, 23, 59, 59))
                .ingestedAt(LocalDateTime.now())
                .build();
    }
}
