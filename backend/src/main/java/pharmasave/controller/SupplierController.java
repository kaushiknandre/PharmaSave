package pharmasave.controller;

import pharmasave.entity.Supplier;
import pharmasave.exception.ResourceNotFoundException;
import pharmasave.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierRepository supplierRepository;

    @GetMapping
    public ResponseEntity<List<Supplier>> getAll() {
        return ResponseEntity.ok(supplierRepository.findByIsActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id)));
    }

    @PostMapping
    public ResponseEntity<Supplier> create(@RequestBody Supplier supplier) {
        if (supplierRepository.existsByEmail(supplier.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + supplier.getEmail());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierRepository.save(supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> update(@PathVariable Long id, @RequestBody Supplier updated) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        existing.setName(updated.getName());
        existing.setContactPerson(updated.getContactPerson());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setAddress(updated.getAddress());
        existing.setGstNumber(updated.getGstNumber());
        return ResponseEntity.ok(supplierRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        supplier.setIsActive(false);
        supplierRepository.save(supplier);
        return ResponseEntity.noContent().build();
    }
}
