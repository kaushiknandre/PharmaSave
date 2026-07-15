package pharmasave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {

    private String medicineName;
    private String category;
    private String supermarket;
    private BigDecimal sellingPrice;
    private LocalDate expiryDate;
}