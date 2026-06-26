package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final MedicineRepository medicineRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public Sale createSale(Sale sale, List<SaleItem> items) {
        // Validate and deduct stock for each item
        for (SaleItem item : items) {
            Medicine medicine = medicineRepository.findById(item.getMedicine().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + item.getMedicine().getId()));

            if (medicine.getQuantityInStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for: " + medicine.getName()
                        + ". Available: " + medicine.getQuantityInStock());
            }

            // Deduct stock
            medicine.setQuantityInStock(medicine.getQuantityInStock() - item.getQuantity());
            medicineRepository.save(medicine);

            // Set prices from current medicine data
            item.setUnitPrice(medicine.getSellingPrice());
            BigDecimal discountFactor = BigDecimal.ONE.subtract(
                    item.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
            item.setTotalPrice(medicine.getSellingPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .multiply(discountFactor));
            item.setSale(sale);
        }

        // Calculate totals
        BigDecimal subtotal = items.stream()
                .map(SaleItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sale.setSubtotal(subtotal);

        BigDecimal totalAfterDiscount = subtotal.subtract(sale.getDiscountAmount());
        BigDecimal gstAmount = totalAfterDiscount.multiply(BigDecimal.valueOf(0.18)); // default 18% GST
        sale.setGstAmount(gstAmount);
        sale.setTotalAmount(totalAfterDiscount.add(gstAmount));
        sale.setInvoiceNumber(generateInvoiceNumber());
        sale.setSaleDate(LocalDateTime.now());

        Sale savedSale = saleRepository.save(sale);
        items.forEach(item -> item.setSale(savedSale));
        saleItemRepository.saveAll(items);

        return savedSale;
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
    }

    public List<Sale> getSalesByDateRange(LocalDateTime from, LocalDateTime to) {
        return saleRepository.findBySaleDateBetween(from, to);
    }

    public List<Sale> getSalesByCustomer(Long customerId) {
        return saleRepository.findByCustomerId(customerId);
    }

    public BigDecimal getTotalRevenue(LocalDateTime from, LocalDateTime to) {
        return saleRepository.sumTotalAmountBetween(from, to);
    }

    public Long countSales(LocalDateTime from, LocalDateTime to) {
        return saleRepository.countSalesBetween(from, to);
    }

    private String generateInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "INV-" + timestamp;
    }
}
