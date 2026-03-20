package com.billing.billingapp.product.controller;

import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.dto.ProductRequestDto;
import com.billing.billingapp.product.dto.ProductResponseDto;
import com.billing.billingapp.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /*
     CREATE PRODUCT
     */
    @PostMapping
    public ProductResponseDto createProduct(
            @Valid @RequestBody ProductRequestDto dto
    ) {
        return productService.createProduct(dto);
    }

    /*
     GET ALL PRODUCTS
     */
    @GetMapping
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts();
    }

    /*
     GET PRODUCT BY ID
     */
    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    /*
     UPDATE PRODUCT
     */
    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto dto
    ) {
        return productService.updateProduct(id, dto);
    }

    /*
     DISABLE PRODUCT
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableProduct(@PathVariable Long id) {
        productService.disableProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }
}