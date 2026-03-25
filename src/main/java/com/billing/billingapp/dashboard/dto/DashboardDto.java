package com.billing.billingapp.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardDto {

    private BigDecimal totalReceived;
    private BigDecimal totalPending;

}