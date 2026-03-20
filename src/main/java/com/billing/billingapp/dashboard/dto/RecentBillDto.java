package com.billing.billingapp.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RecentBillDto {

    String getBillNumber();

    LocalDateTime getBillDate();

    BigDecimal getTotalAmount();
}