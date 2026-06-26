package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions", indexes = {
        @Index(name = "idx_promotions_product", columnList = "product_id"),
        @Index(name = "idx_promotions_supermarket", columnList = "supermarket_id"),
        @Index(name = "idx_promotions_window", columnList = "promo_start,promo_end"),
        @Index(name = "idx_promotions_price", columnList = "promo_price")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supermarket_id", nullable = false)
    private Supermarket supermarket;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "external_sku", nullable = false)
    private String externalSku;

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "promo_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal promoPrice;

    @Column(name = "shelf_life_days", nullable = false)
    private Integer shelfLifeDays;

    @Column(name = "promo_start", nullable = false)
    private LocalDateTime promoStart;

    @Column(name = "promo_end", nullable = false)
    private LocalDateTime promoEnd;

    @Column(name = "ingested_at", nullable = false)
    private LocalDateTime ingestedAt;
}
