package com.billing.billingapp.product.service;

import com.billing.billingapp.common.exception.ResourceNotFoundException;
import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void disableProduct(Long id) {
        Product product = productRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        product.setActive(false);
        productRepository.save(product);
    }
}
