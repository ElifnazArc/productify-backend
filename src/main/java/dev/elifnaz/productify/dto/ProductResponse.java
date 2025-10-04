package dev.elifnaz.productify.dto;

import dev.elifnaz.productify.model.Product;
import dev.elifnaz.productify.model.ProductImages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private double popularityScore;
    private double weight;
    private ProductImages images;
    private Double price;

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .popularityScore(product.getPopularityScore())
                .weight(product.getWeight())
                .images(product.getImages())
                .price(product.getPrice())
                .build();
    }
}
