package pharmasave.repository;

import pharmasave.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByInvoiceNumber(String invoiceNumber);
    List<Sale> findByCustomerId(Long customerId);
    List<Sale> findByCreatedById(Long userId);
    List<Sale> findBySaleDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :from AND :to AND s.status = 'COMPLETED'")
    BigDecimal sumTotalAmountBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :from AND :to")
    Long countSalesBetween(LocalDateTime from, LocalDateTime to);
}
