package com.billing.billingapp.product.controller;

import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.ProductRepository;
import com.billing.billingapp.product.dto.ProductRequestDto;
import com.billing.billingapp.product.dto.ProductResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //post method for product creation
    @PostMapping
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto dto) {

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
        product.setHsnCode(dto.getHsnCode());

        Product saved = productRepository.save(product);

        ProductResponseDto response = new ProductResponseDto();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setSku(saved.getSku());
        response.setBarcode(saved.getBarcode());
        response.setSellingPrice(saved.getSellingPrice());
        response.setTaxPercent(saved.getTaxPercent());
        response.setDiscountPercent(saved.getDiscountPercent());
        response.setStockQuantity(saved.getStockQuantity());
        response.setUnit(saved.getUnit());
        response.setCategory(saved.getCategory());
        response.setBrand(saved.getBrand());
        response.setBatchNumber(saved.getBatchNumber());
        response.setExpiryDate(saved.getExpiryDate());
        response.setActive(saved.getActive());

        return response;
    }

    //listing product method
    @GetMapping
    public List<ProductResponseDto> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(product -> {
                    ProductResponseDto dto = new ProductResponseDto();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setSku(product.getSku());
                    dto.setBarcode(product.getBarcode());
                    dto.setSellingPrice(product.getSellingPrice());
                    dto.setTaxPercent(product.getTaxPercent());
                    dto.setDiscountPercent(product.getDiscountPercent());
                    dto.setStockQuantity(product.getStockQuantity());
                    dto.setUnit(product.getUnit());
                    dto.setCategory(product.getCategory());
                    dto.setBrand(product.getBrand());
                    dto.setBatchNumber(product.getBatchNumber());
                    dto.setExpiryDate(product.getExpiryDate());
                    dto.setActive(product.getActive());
                    return dto;
                })
                .toList();
    }

//    get product by id method
    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setBarcode(product.getBarcode());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setTaxPercent(product.getTaxPercent());
        dto.setDiscountPercent(product.getDiscountPercent());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setUnit(product.getUnit());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setBatchNumber(product.getBatchNumber());
        dto.setExpiryDate(product.getExpiryDate());
        dto.setActive(product.getActive());

        return dto;
    }

    //put mappind update product
    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDto dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

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
        product.setHsnCode(dto.getHsnCode());

        Product updated = productRepository.save(product);

        ProductResponseDto response = new ProductResponseDto();
        response.setId(updated.getId());
        response.setName(updated.getName());
        response.setSellingPrice(updated.getSellingPrice());
        response.setStockQuantity(updated.getStockQuantity());
        response.setActive(updated.getActive());

        return response;
    }

    @PatchMapping("/{id}/disable")
    public void disableProduct(@PathVariable Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setActive(false);
        productRepository.save(product);
    }


}
