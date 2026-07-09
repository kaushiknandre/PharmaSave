package pharmasave.controller;

import pharmasave.entity.Sale;
import pharmasave.entity.SaleItem;
import pharmasave.service.SaleService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saleService.createSale(request.getSale(), request.getItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSale(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @GetMapping
    public ResponseEntity<List<Sale>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(saleService.getSalesByDateRange(from, to));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Sale>> getSalesByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(saleService.getSalesByCustomer(customerId));
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        BigDecimal revenue = saleService.getTotalRevenue(from, to);
        Long count = saleService.countSales(from, to);
        return ResponseEntity.ok(Map.of("totalRevenue", revenue, "totalSales", count, "from", from, "to", to));
    }

    @Data
    public static class SaleRequest {
        private Sale sale;
        private List<SaleItem> items;
    }
}
