package com.billing.billingapp.product.controller;

import com.billing.billingapp.common.exception.ResourceNotFoundException;
import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.ProductRepository;
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

    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    //post method for product creation
    @PostMapping
    public ProductResponseDto createProduct(
            @Valid @RequestBody ProductRequestDto dto
    ) {
        Product product = mapToEntity(dto);
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    //listing product method
    @GetMapping
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //    get product by id method
    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {

        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id)
                );

        return mapToResponse(product);
    }

    //put mappind update product
    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto dto
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));


        // update fields (DO NOT create new Product)
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setMrp(dto.getMrp());
        product.setTaxPercent(dto.getTaxPercent());
        product.setDiscountPercent(dto.getDiscountPercent());
        product.setStockQuantity(dto.getStockQuantity());
        product.setReorderLevel(dto.getReorderLevel());
        product.setUnit(dto.getUnit());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
        product.setBatchNumber(dto.getBatchNumber());
        product.setExpiryDate(dto.getExpiryDate());

        Product updated = productRepository.save(product);

        return mapToResponse(updated);
    }


    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableProduct(@PathVariable Long id) {
        productService.disableProduct(id);
        return ResponseEntity.noContent().build();
    }

    private Product mapToEntity(ProductRequestDto dto) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setMrp(dto.getMrp());
        product.setTaxPercent(dto.getTaxPercent());
        product.setDiscountPercent(dto.getDiscountPercent());
        product.setStockQuantity(dto.getStockQuantity());
        product.setReorderLevel(dto.getReorderLevel());
        product.setUnit(dto.getUnit());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
        product.setBatchNumber(dto.getBatchNumber());
        product.setExpiryDate(dto.getExpiryDate());

        product.setActive(true); // important

        return product;
    }

    private ProductResponseDto mapToResponse(Product product) {
        ProductResponseDto res = new ProductResponseDto();

        res.setId(product.getId());
        res.setName(product.getName());
        res.setSku(product.getSku());
        res.setBarcode(product.getBarcode());
        res.setSellingPrice(product.getSellingPrice());
        res.setTaxPercent(product.getTaxPercent());
        res.setDiscountPercent(product.getDiscountPercent());
        res.setStockQuantity(product.getStockQuantity());
        res.setUnit(product.getUnit());
        res.setCategory(product.getCategory());
        res.setBrand(product.getBrand());
        res.setBatchNumber(product.getBatchNumber());
        res.setExpiryDate(product.getExpiryDate());
        res.setActive(product.getActive());

        return res;
    }


}
