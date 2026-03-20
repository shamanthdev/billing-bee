package com.billing.billingapp.report.dto;

import com.billing.billingapp.billing.entity.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SalesReportDto {

    private String billNumber;
    private LocalDateTime billDate;
    private String customerName;
    private BigDecimal total;
    private BillStatus status;

    public SalesReportDto(
            String billNumber,
            LocalDateTime billDate,
            String customerName,
            BigDecimal total,
            BillStatus status
    ) {
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.customerName = customerName;
        this.total = total;
        this.status = status;
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

    public BillStatus getStatus() {
        return status;
    }
}