package pharmasave.repository;

import pharmasave.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByPurchaseOrderNumber(String purchaseOrderNumber);
    List<Purchase> findBySupplierId(Long supplierId);
    List<Purchase> findByPurchaseDateBetween(LocalDateTime from, LocalDateTime to);
    List<Purchase> findByStatus(Purchase.PurchaseStatus status);
}
