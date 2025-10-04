package dev.elifnaz.productify.repository;

import dev.elifnaz.productify.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Sorting methods
    List<Product> findAllByOrderByPopularityScoreDesc();

    List<Product> findAllByOrderByPopularityScoreAsc();

    // Search by name
    List<Product> findByNameContainingIgnoreCase(String name);

    // Filtering by popularity score
    List<Product> findByPopularityScoreBetween(double minScore, double maxScore);

    List<Product> findByPopularityScoreGreaterThanEqual(double minScore);

    List<Product> findByPopularityScoreLessThanEqual(double maxScore);
}
