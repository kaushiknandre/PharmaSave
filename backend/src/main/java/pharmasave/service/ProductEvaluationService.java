package pharmasave.service;

import pharmasave.dto.ProductEvaluationResponse;
import pharmasave.entity.Medicine;
import pharmasave.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductEvaluationService {

    private final MedicineRepository medicineRepository;

    public ProductEvaluationResponse evaluateProduct(String sku) {

        List<Medicine> medicines = medicineRepository.findBySku(sku);

        if (medicines.isEmpty()) {
            throw new RuntimeException("No medicines found with SKU: " + sku);
        }

        Medicine bestMedicine = medicines.stream()

                // Lowest Price
                .min(
                        Comparator.comparing(Medicine::getSellingPrice)

                                // If price is same,
                                // choose longest shelf life
                                .thenComparing(Medicine::getExpiryDate,
                                        Comparator.reverseOrder())
                )
                .orElseThrow();

        return ProductEvaluationResponse.builder()
                .sku(bestMedicine.getSku())
                .medicineName(bestMedicine.getName())
                .supermarket(bestMedicine.getSupermarket().name())
                .sellingPrice(bestMedicine.getSellingPrice())
                .expiryDate(bestMedicine.getExpiryDate())
                .build();
    }
}