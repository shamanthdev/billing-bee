package com.billing.billingapp.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailySalesDto {

    LocalDate getDate();

    BigDecimal getSales();
}