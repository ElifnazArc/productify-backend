package dev.elifnaz.productify.controller;

import dev.elifnaz.productify.dto.ProductFilterRequest;
import dev.elifnaz.productify.dto.ProductResponse;
import dev.elifnaz.productify.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts()
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/by-popularity")
    public ResponseEntity<List<ProductResponse>> getProductsByPopularity(
            @RequestParam(defaultValue = "desc") String order) {
        boolean descending = "desc".equalsIgnoreCase(order);
        List<ProductResponse> products = productService.getProductsByPopularity(descending)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String name) {
        List<ProductResponse> products = productService.searchProductsByName(name)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> filterProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minPopularity,
            @RequestParam(required = false) Double maxPopularity) {

        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minPopularity(minPopularity)
                .maxPopularity(maxPopularity)
                .build();

        List<ProductResponse> products = productService.filterProducts(filterRequest)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/filter/price")
    public ResponseEntity<List<ProductResponse>> filterByPrice(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<ProductResponse> products = productService.filterByPriceRange(minPrice, maxPrice)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/filter/popularity")
    public ResponseEntity<List<ProductResponse>> filterByPopularity(
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore) {
        List<ProductResponse> products = productService.filterByPopularityScore(minScore, maxScore)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
}
