package pharmasave.service;

import pharmasave.entity.Medicine;
import pharmasave.exception.ResourceNotFoundException;
import pharmasave.repository.CategoryRepository;
import pharmasave.repository.MedicineRepository;
import pharmasave.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public List<Medicine> getAllActiveMedicines() {
        return medicineRepository.findByIsActiveTrue();
    }

    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + id));
    }

    public List<Medicine> searchMedicines(String name) {
        return medicineRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Medicine> getMedicinesByCategory(Long categoryId) {
        return medicineRepository.findByCategoryId(categoryId);
    }

    public List<Medicine> getLowStockMedicines() {
        return medicineRepository.findLowStockMedicines();
    }

    public List<Medicine> getExpiringMedicines(int withinDays) {
        return medicineRepository.findMedicinesExpiringBefore(LocalDate.now().plusDays(withinDays));
    }

    public Medicine createMedicine(Medicine medicine) {
        if (medicine.getSku() == null || medicine.getSku().isBlank()) {
            medicine.setSku(generateSku(medicine));
        } else if (medicineRepository.existsBySku(medicine.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + medicine.getSku());
        }
        return medicineRepository.save(medicine);
    }

    public Medicine updateMedicine(Long id, Medicine updated) {
        Medicine existing = getMedicineById(id);
        existing.setName(updated.getName());
        existing.setGenericName(updated.getGenericName());
        existing.setBrandName(updated.getBrandName());
        existing.setDescription(updated.getDescription());
        existing.setDosageForm(updated.getDosageForm());
        existing.setStrength(updated.getStrength());
        existing.setPurchasePrice(updated.getPurchasePrice());
        existing.setSellingPrice(updated.getSellingPrice());
        existing.setMrp(updated.getMrp());
        existing.setGstPercentage(updated.getGstPercentage());
        existing.setReorderLevel(updated.getReorderLevel());
        existing.setExpiryDate(updated.getExpiryDate());
        existing.setManufactureDate(updated.getManufactureDate());
        existing.setRequiresPrescription(updated.getRequiresPrescription());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getSupplier() != null) existing.setSupplier(updated.getSupplier());
        return medicineRepository.save(existing);
    }

    public void updateStock(Long id, int quantity) {
        Medicine medicine = getMedicineById(id);
        int newQty = medicine.getQuantityInStock() + quantity;
        if (newQty < 0) throw new IllegalArgumentException("Insufficient stock for medicine: " + medicine.getName());
        medicine.setQuantityInStock(newQty);
        medicineRepository.save(medicine);
    }

    public void deactivateMedicine(Long id) {
        Medicine medicine = getMedicineById(id);
        medicine.setIsActive(false);
        medicineRepository.save(medicine);
    }

    private String generateSku(Medicine medicine) {
        String prefix = medicine.getName().substring(0, Math.min(3, medicine.getName().length())).toUpperCase();
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
