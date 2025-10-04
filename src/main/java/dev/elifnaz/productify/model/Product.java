package dev.elifnaz.productify.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double popularityScore;
    private double weight;

    @Embedded
    private ProductImages images;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double price;
}
