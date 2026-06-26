package com.example.demo.repository;

import com.example.demo.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByIsActiveTrue();

    Optional<Medicine> findBySku(String sku);

    List<Medicine> findByNameContainingIgnoreCase(String name);

    List<Medicine> findByCategoryId(Long categoryId);

    List<Medicine> findBySupplierId(Long supplierId);

    // Low stock alert
    @Query("SELECT m FROM Medicine m WHERE m.quantityInStock <= m.reorderLevel AND m.isActive = true")
    List<Medicine> findLowStockMedicines();

    // Expiry alert — medicines expiring within given date
    @Query("SELECT m FROM Medicine m WHERE m.expiryDate <= :date AND m.isActive = true")
    List<Medicine> findMedicinesExpiringBefore(LocalDate date);

    boolean existsBySku(String sku);
}
