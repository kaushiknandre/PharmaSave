package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@Service
public class PromotionWindowService {

    private final ZoneId businessZone;

    public PromotionWindowService(@Value("${pharmasave.business-zone}") String businessZone) {
        this.businessZone = ZoneId.of(businessZone);
    }

    public LocalDateTime now() {
        return LocalDateTime.now(businessZone);
    }

    public PromotionCycle currentCycle() {
        LocalDate today = LocalDate.now(businessZone);
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY));
        LocalDate endDate = startDate.plusDays(6);
        return new PromotionCycle(
                LocalDateTime.of(startDate, LocalTime.MIDNIGHT),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59))
        );
    }

    public boolean isStrictWeeklyCycle(LocalDateTime start, LocalDateTime end) {
        return start != null
                && end != null
                && start.getDayOfWeek() == DayOfWeek.THURSDAY
                && start.toLocalTime().equals(LocalTime.MIDNIGHT)
                && end.getDayOfWeek() == DayOfWeek.WEDNESDAY
                && end.toLocalTime().equals(LocalTime.of(23, 59, 59))
                && end.toLocalDate().equals(start.toLocalDate().plusDays(6));
    }

    public record PromotionCycle(LocalDateTime start, LocalDateTime end) {
    }
}
