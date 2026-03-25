package com.billing.billingapp.billing.service;

import com.billing.billingapp.billing.dto.*;
import com.billing.billingapp.billing.entity.*;
import com.billing.billingapp.billing.repository.BillRepository;
import com.billing.billingapp.common.exception.InsufficientStockException;
import com.billing.billingapp.common.exception.ResourceNotFoundException;
import com.billing.billingapp.customer.entity.Customer;
import com.billing.billingapp.customer.repository.CustomerRepository;
import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public BillService(BillRepository billRepository,
                       ProductRepository productRepository,
                       CustomerRepository customerRepository) {
        this.billRepository = billRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    /* ==================== CREATE BILL ==================== */
    @Transactional
    public Long createBill(CreateBillRequestDto dto) {

        validateCreateRequest(dto);

        Bill bill = new Bill();
        bill.setBillNumber(generateBillNumber());
        bill.setBillDate(LocalDateTime.now());
        bill.setStatus(BillStatus.ACTIVE);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;

        for (BillItemRequestDto itemDto : dto.getItems()) {

            Product product = getProduct(itemDto.getProductId());

            int qty = itemDto.getQuantity();
            validateStock(product, qty);

            BigDecimal lineAmount = product.getSellingPrice().multiply(BigDecimal.valueOf(qty));
            BigDecimal gstPercent = product.getTaxPercent() != null ? product.getTaxPercent() : BigDecimal.ZERO;
            BigDecimal gstAmount = lineAmount.multiply(gstPercent).divide(BigDecimal.valueOf(100));

            BillItem item = new BillItem();
            item.setBill(bill);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getSellingPrice());
            item.setQuantity(qty);
            item.setLineTotal(lineAmount);
            item.setGstPercent(gstPercent);
            item.setGstAmount(gstAmount);

            bill.getItems().add(item);

            subtotal = subtotal.add(lineAmount);
            totalGst = totalGst.add(gstAmount);

            updateStock(product, qty, false);
        }

        BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
        validateDiscount(discount, subtotal);

        setCustomerDetails(bill, dto);

        BigDecimal total = subtotal.subtract(discount).add(totalGst);

        bill.setSubtotal(subtotal);
        bill.setDiscount(discount);
        bill.setGstAmount(totalGst);
        bill.setTotal(total);

        applyPaymentLogic(bill, dto.getPaymentType(), total);

        return billRepository.save(bill).getId();
    }

    /* ==================== GET BILL ==================== */
    public BillResponseDto getBillById(Long id) {
        Bill bill = getBill(id);
        return mapToBillResponse(bill);
    }

    /* ==================== GET LIST ==================== */
    public Page<BillListResponseDto> getBills(int page, int size, String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "billDate"));

        return billRepository
                .findAllActive((search == null || search.isBlank()) ? null : search, pageable)
                .map(this::mapToListDto);
    }

    /* ==================== CANCEL ==================== */
    @Transactional
    public void cancelBill(Long billId) {

        Bill bill = getBill(billId);

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new IllegalStateException("Bill already cancelled");
        }

        for (BillItem item : bill.getItems()) {
            Product product = getProduct(item.getProductId());
            updateStock(product, item.getQuantity(), true);
        }

        bill.setStatus(BillStatus.CANCELLED);
        billRepository.save(bill);
    }

    /* ==================== UPDATE ==================== */
    @Transactional
    public void updateBill(Long billId, CreateBillRequestDto dto) {

        Bill bill = getBill(billId);

        if (bill.getStatus() != BillStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE bills can be edited");
        }

        // Restore stock
        for (BillItem item : bill.getItems()) {
            Product product = getProduct(item.getProductId());
            updateStock(product, item.getQuantity(), true);
        }

        bill.getItems().clear();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;

        for (BillItemRequestDto itemDto : dto.getItems()) {

            Product product = getProduct(itemDto.getProductId());

            int qty = itemDto.getQuantity();
            validateStock(product, qty);

            BigDecimal lineAmount = product.getSellingPrice().multiply(BigDecimal.valueOf(qty));
            BigDecimal gstPercent = product.getTaxPercent() != null ? product.getTaxPercent() : BigDecimal.ZERO;
            BigDecimal gstAmount = lineAmount.multiply(gstPercent).divide(BigDecimal.valueOf(100));

            BillItem item = new BillItem();
            item.setBill(bill);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getSellingPrice());
            item.setQuantity(qty);
            item.setLineTotal(lineAmount);
            item.setGstPercent(gstPercent);
            item.setGstAmount(gstAmount);

            bill.getItems().add(item);

            subtotal = subtotal.add(lineAmount);
            totalGst = totalGst.add(gstAmount);

            updateStock(product, qty, false);
        }

        BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
        validateDiscount(discount, subtotal);

        BigDecimal total = subtotal.subtract(discount).add(totalGst);

        bill.setSubtotal(subtotal);
        bill.setDiscount(discount);
        bill.setGstAmount(totalGst);
        bill.setTotal(total);

        applyPaymentLogic(bill, dto.getPaymentType(), total);

        billRepository.save(bill);
    }

    /* ==================== MAPPERS ==================== */

    private BillResponseDto mapToBillResponse(Bill bill) {

        BillResponseDto dto = new BillResponseDto();

        dto.setId(bill.getId());
        dto.setBillNumber(bill.getBillNumber());
        dto.setBillDate(bill.getBillDate());
        dto.setCustomerId(bill.getCustomerId());
        dto.setCustomerName(bill.getCustomerName());
        dto.setSubtotal(bill.getSubtotal());
        dto.setDiscount(bill.getDiscount());
        dto.setGstAmount(bill.getGstAmount());
        dto.setTotal(bill.getTotal());
        dto.setStatus(bill.getStatus().name());

        // 🔥 IMPORTANT
        dto.setPaymentType(bill.getPaymentType().name());
        dto.setBalanceAmount(bill.getBalanceAmount());

        dto.setItems(
                bill.getItems().stream()
                        .map(this::mapBillItem)
                        .toList()
        );

        return dto;
    }

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

    private BillListResponseDto mapToListDto(Bill bill) {

        BillListResponseDto dto = new BillListResponseDto();

        dto.setId(bill.getId());
        dto.setBillNumber(bill.getBillNumber());
        dto.setBillDate(bill.getBillDate());
        dto.setCustomerName(bill.getCustomerName());
        dto.setSubtotal(bill.getSubtotal());
        dto.setDiscount(bill.getDiscount());
        dto.setGstAmount(bill.getGstAmount());
        dto.setTotal(bill.getTotal());
        dto.setStatus(bill.getStatus().name());

        // 🔥 IMPORTANT
        dto.setPaymentType(bill.getPaymentType().name());
        dto.setBalanceAmount(bill.getBalanceAmount());

        return dto;
    }

    /* ==================== HELPERS ==================== */

    private void validateCreateRequest(CreateBillRequestDto dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Bill items cannot be empty");
        }
        if (dto.getCustomerId() == null && dto.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Customer or phone number required");
        }
        if (dto.getPaymentType() == null) {
            throw new IllegalArgumentException("Payment type is required");
        }
    }

    private Product getProduct(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Bill getBill(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
    }

    private void validateStock(Product product, int qty) {
        if (qty > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Only " + product.getStockQuantity() + " units available for " + product.getName()
            );
        }
    }

    private void updateStock(Product product, int qty, boolean restore) {
        int updated = restore
                ? product.getStockQuantity() + qty
                : product.getStockQuantity() - qty;

        product.setStockQuantity(updated);
        productRepository.save(product);
    }

    private void validateDiscount(BigDecimal discount, BigDecimal subtotal) {
        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed subtotal");
        }
    }

    private void setCustomerDetails(Bill bill, CreateBillRequestDto dto) {

        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findByIdAndActiveTrue(dto.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid customer selected"));

            bill.setCustomerId(customer.getId());
            bill.setCustomerName(customer.getName());
            bill.setPhoneNumber(customer.getPhone());

        } else {
            bill.setCustomerName("Customer - " + dto.getPhoneNumber());
            bill.setPhoneNumber(dto.getPhoneNumber());
        }
    }

    private void applyPaymentLogic(Bill bill, String paymentTypeStr, BigDecimal total) {

        PaymentType paymentType = PaymentType.valueOf(paymentTypeStr.toUpperCase());

        bill.setPaymentType(paymentType);

        if (paymentType == PaymentType.PAID) {
            bill.setBalanceAmount(BigDecimal.ZERO);
        } else {
            bill.setBalanceAmount(total);
        }
    }

    private String generateBillNumber() {
        return "BILL-" + System.currentTimeMillis();
    }

    @Transactional
    public void payBill(Long billId, PayBillRequestDto dto) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found with id: " + billId)
                );

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay a cancelled bill");
        }

        if (bill.getPaymentType() == PaymentType.PAID) {
            throw new IllegalStateException("Bill is already fully paid");
        }

        BigDecimal payAmount = dto.getAmount();

        if (payAmount == null || payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }

        BigDecimal currentBalance = bill.getBalanceAmount();

        if (payAmount.compareTo(currentBalance) > 0) {
            throw new IllegalArgumentException("Payment exceeds pending amount");
        }

        // 🔥 Deduct payment
        BigDecimal newBalance = currentBalance.subtract(payAmount);
        bill.setBalanceAmount(newBalance);

        // 🔥 Update status
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            bill.setPaymentType(PaymentType.PAID);
        } else {
            bill.setPaymentType(PaymentType.CREDIT);
        }

        billRepository.save(bill);
    }
}