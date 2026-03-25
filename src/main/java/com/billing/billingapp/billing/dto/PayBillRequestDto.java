package com.billing.billingapp.billing.dto;

import java.math.BigDecimal;

public class PayBillRequestDto {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}