package dev.elifnaz.productify.service;

import dev.elifnaz.productify.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for calculating product prices based on gold prices
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PriceCalculator {

    private final GoldPriceService goldPriceService;

    /**
     * Calculate price for a single product
     * Formula: (popularityScore + 1) * weight * goldPricePerGram
     */
    public double calculatePrice(Product product) {
        double goldPricePerGram = goldPriceService.getGoldPricePerGram();
        return (product.getPopularityScore() + 1) * product.getWeight() * goldPricePerGram;
    }

    /**
     * Calculate and set prices for a list of products
     */
    public void calculatePrices(List<Product> products) {
        double goldPricePerGram = goldPriceService.getGoldPricePerGram();
        log.debug("Calculating prices for {} products at ${} per gram", products.size(), goldPricePerGram);

        products.forEach(product -> {
            double price = (product.getPopularityScore() + 1) * product.getWeight() * goldPricePerGram;
            product.setPrice(price);
        });
    }
}
