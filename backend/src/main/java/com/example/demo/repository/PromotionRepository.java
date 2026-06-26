package com.example.demo.repository;

import com.example.demo.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
            SELECT p FROM Promotion p
            JOIN FETCH p.product
            JOIN FETCH p.supermarket
            WHERE p.promoStart <= :now AND p.promoEnd >= :now
            """)
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    boolean existsByExternalSkuAndPromoStartAndSupermarket_Code(String externalSku, LocalDateTime promoStart, String supermarketCode);
}
