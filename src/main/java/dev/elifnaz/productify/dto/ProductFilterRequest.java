package dev.elifnaz.productify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    private Double minPrice;
    private Double maxPrice;
    private Double minPopularity;
    private Double maxPopularity;

    public boolean hasPriceFilter() {
        return minPrice != null || maxPrice != null;
    }
}
