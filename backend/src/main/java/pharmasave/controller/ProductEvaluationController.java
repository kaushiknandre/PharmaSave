package pharmasave.controller;

import pharmasave.dto.ProductEvaluationResponse;
import pharmasave.service.ProductEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class ProductEvaluationController {

    private final ProductEvaluationService productEvaluationService;

    @GetMapping("/{sku}")
    public ProductEvaluationResponse evaluateProduct(
            @PathVariable String sku) {

        return productEvaluationService.evaluateProduct(sku);
    }
}