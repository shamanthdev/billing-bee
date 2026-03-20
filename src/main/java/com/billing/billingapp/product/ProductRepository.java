package com.billing.billingapp.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    Optional<Product> findByIdAndActiveTrue(Long id);
    @Query("""
    SELECT p.sku 
    FROM Product p 
    WHERE p.sku IS NOT NULL 
    ORDER BY p.id DESC 
    LIMIT 1
    """)
    String findLastSku();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.reorderLevel ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts();
}
