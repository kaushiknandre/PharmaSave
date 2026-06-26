package com.example.demo.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class PromotionWindowServiceTest {

    private final PromotionWindowService service = new PromotionWindowService("UTC");

    @Test
    void acceptsStrictThursdayToWednesdayCycle() {
        assertThat(service.isStrictWeeklyCycle(
                LocalDateTime.of(2026, 6, 25, 0, 0, 0),
                LocalDateTime.of(2026, 7, 1, 23, 59, 59)
        )).isTrue();
    }

    @Test
    void rejectsPromotionsOutsideBoundarySeconds() {
        assertThat(service.isStrictWeeklyCycle(
                LocalDateTime.of(2026, 6, 25, 0, 0, 1),
                LocalDateTime.of(2026, 7, 1, 23, 59, 59)
        )).isFalse();
        assertThat(service.isStrictWeeklyCycle(
                LocalDateTime.of(2026, 6, 25, 0, 0, 0),
                LocalDateTime.of(2026, 7, 2, 0, 0, 0)
        )).isFalse();
    }
}
