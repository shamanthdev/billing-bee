package com.billing.billingapp.billing.service;

import com.billing.billingapp.billing.dto.*;
import com.billing.billingapp.billing.entity.Bill;
import com.billing.billingapp.billing.entity.BillItem;
import com.billing.billingapp.billing.entity.BillStatus;
import com.billing.billingapp.billing.repository.BillRepository;
import com.billing.billingapp.common.exception.InsufficientStockException;
import com.billing.billingapp.common.exception.ResourceNotFoundException;
import com.billing.billingapp.customer.entity.Customer;
import com.billing.billingapp.customer.repository.CustomerRepository;
import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public BillService(
            BillRepository billRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository) {
        this.billRepository = billRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    /* -------------------- CREATE BILL -------------------- */
    @Transactional
    public Long createBill(CreateBillRequestDto dto) {

        Bill bill = new Bill();
        bill.setBillNumber(generateBillNumber());
        bill.setBillDate(LocalDateTime.now());
        bill.setStatus(BillStatus.ACTIVE);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;

        for (BillItemRequestDto itemDto : dto.getItems()) {

            Product product = productRepository
                    .findByIdAndActiveTrue(itemDto.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found with id: " + itemDto.getProductId()
                            )
                    );

            int requestedQty = itemDto.getQuantity();
            int availableStock = product.getStockQuantity();

            // 🔴 STOCK VALIDATION (FIX #1)
            if (requestedQty > availableStock) {
                throw new InsufficientStockException(
                        "Only " + availableStock + " units available for "
                                + product.getName()
                );
            }

            BigDecimal lineAmount =
                    product.getSellingPrice()
                            .multiply(BigDecimal.valueOf(requestedQty));

            BigDecimal gstPercent =
                    product.getTaxPercent() != null
                            ? product.getTaxPercent()
                            : BigDecimal.ZERO;

            BigDecimal gstAmount =
                    lineAmount.multiply(gstPercent)
                            .divide(BigDecimal.valueOf(100));

            BillItem item = new BillItem();
            item.setBill(bill);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getSellingPrice());
            item.setQuantity(requestedQty);
            item.setLineTotal(lineAmount);
            item.setGstPercent(gstPercent);
            item.setGstAmount(gstAmount);

            bill.getItems().add(item);

            subtotal = subtotal.add(lineAmount);
            totalGst = totalGst.add(gstAmount);

            // 🔴 STOCK DEDUCTION (FIX #2)
            product.setStockQuantity(availableStock - requestedQty);
            productRepository.save(product);
        }

        BigDecimal discount =
                dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;

        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed subtotal");
        }

        Customer customer = customerRepository
                .findByIdAndActiveTrue(dto.getCustomerId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid customer selected")
                );

        bill.setCustomerId(customer.getId());
        bill.setCustomerName(customer.getName());
        bill.setSubtotal(subtotal);
        bill.setDiscount(discount);
        bill.setGstAmount(totalGst);
        bill.setTotal(
                subtotal
                        .subtract(discount)
                        .add(totalGst)
        );

        Bill savedBill = billRepository.save(bill);
        return savedBill.getId();
    }

    /* -------------------- GET BILL BY ID -------------------- */
    public BillResponseDto getBillById(Long id) {

        Bill bill = billRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found with id: " + id)
                );

        BillResponseDto dto = new BillResponseDto();
        dto.setId(bill.getId());
        dto.setBillNumber(bill.getBillNumber());
        dto.setBillDate(bill.getBillDate());
        dto.setCustomerId(bill.getCustomerId());
        dto.setCustomerName(bill.getCustomerName());
        dto.setSubtotal(bill.getSubtotal());
        dto.setDiscount(bill.getDiscount());
        dto.setStatus(bill.getStatus().name());
        dto.setGstAmount(bill.getGstAmount());
        dto.setTotal(bill.getTotal());


        dto.setItems(
                bill.getItems().stream()
                        .map(this::mapBillItem)
                        .toList()
        );

        return dto;
    }

    /* -------------------- MAP BILL ITEM -------------------- */
    private BillItemResponseDto mapBillItem(BillItem item) {

        BillItemResponseDto dto = new BillItemResponseDto();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setLineTotal(item.getLineTotal());
        dto.setGstPercent(item.getGstPercent());
        dto.setGstAmount(item.getGstAmount());

        return dto;
    }

    /* -------------------- GET ALL BILLS (LIST) -------------------- */
    public Page<BillListResponseDto> getBills(
            int page,
            int size,
            String search
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "billDate")
        );

        return billRepository
                .findAllActive(
                        (search == null || search.isBlank()) ? null : search,
                        pageable
                )
                .map(this::mapToDto);
    }

    private BillListResponseDto mapToDto(Bill bill) {
        BillListResponseDto dto = new BillListResponseDto();
        dto.setId(bill.getId());
        dto.setBillNumber(bill.getBillNumber());
        dto.setBillDate(bill.getBillDate());
        dto.setCustomerId(bill.getCustomerId());
        dto.setCustomerName(bill.getCustomerName());
        dto.setSubtotal(bill.getSubtotal());
        dto.setDiscount(bill.getDiscount());
        dto.setGstAmount(bill.getGstAmount());
        dto.setStatus(bill.getStatus().name());
        dto.setTotal(bill.getTotal());
        return dto;
    }


    @Transactional
    public void cancelBill(Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new IllegalStateException("Bill already cancelled");
        }

        // 🔁 RESTORE STOCK
        for (BillItem item : bill.getItems()) {
            Product product = productRepository
                    .findById(item.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product not found"));

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );

            productRepository.save(product);
        }

        bill.setStatus(BillStatus.CANCELLED);
        billRepository.save(bill);
    }


    @Transactional
    public void updateBill(Long billId, CreateBillRequestDto dto) {

        // 1️⃣ Fetch bill
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found with id: " + billId)
                );

        // 2️⃣ Status validation
        if (bill.getStatus() != BillStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE bills can be edited");
        }

        // 3️⃣ RESTORE STOCK from existing items
        for (BillItem existingItem : bill.getItems()) {
            Product product = productRepository.findById(existingItem.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product not found")
                    );

            product.setStockQuantity(
                    product.getStockQuantity() + existingItem.getQuantity()
            );

            productRepository.save(product);
        }

        // 4️⃣ Clear existing items
        bill.getItems().clear();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;

        // 5️⃣ Rebuild bill items (SAME AS CREATE)
        for (BillItemRequestDto itemDto : dto.getItems()) {

            Product product = productRepository
                    .findByIdAndActiveTrue(itemDto.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found with id: " + itemDto.getProductId()
                            )
                    );

            int requestedQty = itemDto.getQuantity();
            int availableStock = product.getStockQuantity();

            if (requestedQty > availableStock) {
                throw new InsufficientStockException(
                        "Only " + availableStock + " units available for "
                                + product.getName()
                );
            }

            BigDecimal lineAmount =
                    product.getSellingPrice()
                            .multiply(BigDecimal.valueOf(requestedQty));

            BigDecimal gstPercent =
                    product.getTaxPercent() != null
                            ? product.getTaxPercent()
                            : BigDecimal.ZERO;

            BigDecimal gstAmount =
                    lineAmount.multiply(gstPercent)
                            .divide(BigDecimal.valueOf(100));

            BillItem item = new BillItem();
            item.setBill(bill);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getSellingPrice());
            item.setQuantity(requestedQty);
            item.setLineTotal(lineAmount);
            item.setGstPercent(gstPercent);
            item.setGstAmount(gstAmount);

            bill.getItems().add(item);

            subtotal = subtotal.add(lineAmount);
            totalGst = totalGst.add(gstAmount);

            // 🔴 Deduct stock again
            product.setStockQuantity(availableStock - requestedQty);
            productRepository.save(product);
        }

        BigDecimal discount =
                dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;

        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed subtotal");
        }

        bill.setSubtotal(subtotal);
        bill.setDiscount(discount);
        bill.setGstAmount(totalGst);
        bill.setTotal(
                subtotal
                        .subtract(discount)
                        .add(totalGst)
        );

        billRepository.save(bill);
    }

    /* -------------------- INTERNAL -------------------- */
    public Bill getBillEntityById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found with id: " + id)
                );
    }

    private String generateBillNumber() {
        return "BILL-" + System.currentTimeMillis();
    }
}
