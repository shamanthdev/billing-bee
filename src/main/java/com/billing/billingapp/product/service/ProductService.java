package com.billing.billingapp.product.service;

import com.billing.billingapp.common.exception.ResourceNotFoundException;
import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.ProductRepository;
import com.billing.billingapp.product.dto.ProductRequestDto;
import com.billing.billingapp.product.dto.ProductResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /*
     CREATE PRODUCT
     */
    public ProductResponseDto createProduct(ProductRequestDto dto) {

        Product product = new Product();

        product.setName(dto.getName());

        // AUTO GENERATE SKU IF EMPTY
        if (dto.getSku() == null || dto.getSku().trim().isEmpty()) {
            product.setSku(generateSku());
        } else {
            product.setSku(dto.getSku());
        }

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

        // IMPORTANT
        product.setHsnCode(dto.getHsnCode());

        product.setActive(true);

        Product saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    /*
     UPDATE PRODUCT
     */
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        ));

        product.setName(dto.getName());

        if (dto.getSku() == null || dto.getSku().trim().isEmpty()) {
            product.setSku(generateSku());
        } else {
            product.setSku(dto.getSku());
        }

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

        return mapToResponse(updated);
    }

    /*
     GET ALL PRODUCTS
     */
    public List<ProductResponseDto> getAllProducts() {

        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /*
     GET PRODUCT BY ID
     */
    public ProductResponseDto getProductById(Long id) {

        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        ));

        return mapToResponse(product);
    }

    /*
     DISABLE PRODUCT
     */
    public void disableProduct(Long id) {

        Product product = productRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        ));

        product.setActive(false);

        productRepository.save(product);
    }

    /*
     GENERATE SKU
     */
    private String generateSku() {

        String lastSku = productRepository.findLastSku();

        String today = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));

        int nextNumber = 1;

        if (lastSku != null && lastSku.startsWith(today)) {

            String[] parts = lastSku.split("-");

            if (parts.length == 2) {
                nextNumber = Integer.parseInt(parts[1]) + 1;
            }
        }

        return today + "-" + String.format("%04d", nextNumber);
    }
    /*
     ENTITY → RESPONSE DTO
     */
    private ProductResponseDto mapToResponse(Product product) {

        ProductResponseDto res = new ProductResponseDto();

        res.setId(product.getId());
        res.setName(product.getName());
        res.setSku(product.getSku());
        res.setBarcode(product.getBarcode());

        res.setCostPrice(product.getCostPrice());
        res.setSellingPrice(product.getSellingPrice());
        res.setTaxPercent(product.getTaxPercent());
        res.setDiscountPercent(product.getDiscountPercent());

        res.setStockQuantity(product.getStockQuantity());
        res.setUnit(product.getUnit());
        res.setCategory(product.getCategory());
        res.setBrand(product.getBrand());

        res.setBatchNumber(product.getBatchNumber());
        res.setExpiryDate(product.getExpiryDate());

        res.setHsnCode(product.getHsnCode());

        res.setActive(product.getActive());

        return res;
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }
}