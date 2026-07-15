package pharmasave.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pharmasave.dto.RecommendationResponse;
import pharmasave.entity.Category;
import pharmasave.entity.Medicine;
import pharmasave.entity.User;
import pharmasave.exception.ResourceNotFoundException;
import pharmasave.repository.MedicineRepository;
import pharmasave.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final UserPreferenceService preferenceService;

    public RecommendationResponse getRecommendation() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Category favouriteCategory =
                preferenceService.getFavouriteCategory(user.getId());

        if (favouriteCategory == null) {
            throw new ResourceNotFoundException("No user interactions found.");
        }

        List<Medicine> medicines =
                medicineRepository.findByCategoryId(favouriteCategory.getId());

        Medicine bestMedicine = medicines.stream()

                .filter(Medicine::getIsActive)

                .min(
                        Comparator.comparing(Medicine::getSellingPrice)
                                .thenComparing(Medicine::getExpiryDate,
                                        Comparator.reverseOrder())
                )

                .orElseThrow(() ->
                        new ResourceNotFoundException("No medicines found"));

        return RecommendationResponse.builder()
                .medicineName(bestMedicine.getName())
                .category(bestMedicine.getCategory().getName())
                .supermarket(bestMedicine.getSupermarket().name())
                .sellingPrice(bestMedicine.getSellingPrice())
                .expiryDate(bestMedicine.getExpiryDate())
                .build();
    }
}