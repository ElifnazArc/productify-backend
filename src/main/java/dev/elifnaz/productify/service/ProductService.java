package dev.elifnaz.productify.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.elifnaz.productify.dto.ProductFilterRequest;
import dev.elifnaz.productify.model.Product;
import dev.elifnaz.productify.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final PriceCalculator priceCalculator;

    @PostConstruct
    public void loadProducts() {
        if (productRepository.count() > 0) {
            log.info("Products already exist in database, skipping JSON load");
            return;
        }

        try {
            ClassPathResource resource = new ClassPathResource("products.json");
            InputStream inputStream = resource.getInputStream();

            List<Product> loadedProducts = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Product>>() {
                    });

            loadedProducts.forEach(product -> product.setId(null));
            List<Product> savedProducts = productRepository.saveAll(loadedProducts);

            log.info("Successfully loaded and saved {} products to database", savedProducts.size());

        } catch (IOException e) {
            log.error("Error loading products from JSON: {}", e.getMessage(), e);
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        priceCalculator.calculatePrices(products);
        return products;
    }

    public List<Product> getProductsByPopularity(boolean descending) {
        List<Product> products = descending
                ? productRepository.findAllByOrderByPopularityScoreDesc()
                : productRepository.findAllByOrderByPopularityScoreAsc();
        priceCalculator.calculatePrices(products);
        return products;
    }

    public List<Product> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        priceCalculator.calculatePrices(products);
        return products;
    }

    public List<Product> filterByPopularityScore(Double minScore, Double maxScore) {
        List<Product> products = findByPopularityRange(minScore, maxScore);
        priceCalculator.calculatePrices(products);
        return products;
    }

    public List<Product> filterByPriceRange(Double minPrice, Double maxPrice) {
        List<Product> products = productRepository.findAll();
        priceCalculator.calculatePrices(products);

        return products.stream()
                .filter(product -> {
                    if (minPrice != null && product.getPrice() < minPrice)
                        return false;
                    if (maxPrice != null && product.getPrice() > maxPrice)
                        return false;
                    return true;
                })
                .collect(Collectors.toList());
    }

    public List<Product> filterProducts(ProductFilterRequest filterRequest) {
        // Filter by popularity (DB level)
        List<Product> products = findByPopularityRange(
                filterRequest.getMinPopularity(),
                filterRequest.getMaxPopularity());

        // Calculate prices
        priceCalculator.calculatePrices(products);

        // Filter by price (in-memory)
        if (filterRequest.hasPriceFilter()) {
            products = products.stream()
                    .filter(product -> {
                        if (filterRequest.getMinPrice() != null && product.getPrice() < filterRequest.getMinPrice())
                            return false;
                        if (filterRequest.getMaxPrice() != null && product.getPrice() > filterRequest.getMaxPrice())
                            return false;
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        return products;
    }

    private List<Product> findByPopularityRange(Double minScore, Double maxScore) {
        if (minScore != null && maxScore != null) {
            return productRepository.findByPopularityScoreBetween(minScore, maxScore);
        } else if (minScore != null) {
            return productRepository.findByPopularityScoreGreaterThanEqual(minScore);
        } else if (maxScore != null) {
            return productRepository.findByPopularityScoreLessThanEqual(maxScore);
        } else {
            return productRepository.findAll();
        }
    }
}
