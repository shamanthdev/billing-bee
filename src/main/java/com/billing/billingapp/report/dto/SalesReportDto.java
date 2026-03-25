package com.billing.billingapp.report.dto;
import com.billing.billingapp.billing.entity.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

public class SalesReportDto {
    private Long id;
    private String billNumber;
    private LocalDateTime billDate;
    private String customerName;
    private BigDecimal total;
    private PaymentType paymentType;

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public SalesReportDto(
            Long id,
            String billNumber,
            LocalDateTime billDate,
            String customerName,
            BigDecimal total,
            PaymentType paymentType
    ) {
        this.id = id;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.customerName = customerName;
        this.total = total;
        this.paymentType = paymentType;
    }
    public Long getId() {
        return id;
    }
    public String getBillNumber() {
        return billNumber;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getTotal() {
        return total;
    }

}