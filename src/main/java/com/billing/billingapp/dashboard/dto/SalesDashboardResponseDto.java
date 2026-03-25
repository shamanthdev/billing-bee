package com.billing.billingapp.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public class SalesDashboardResponseDto {

    private BigDecimal  totalSales;
    private BigDecimal todaySales;
    private Long totalBills;
    private List<RecentBillDto> recentBills;
    private List<DailySalesDto> dailySales;

    public SalesDashboardResponseDto() {
    }

    public SalesDashboardResponseDto(
            BigDecimal  totalSales,
            BigDecimal  todaySales,
            Long totalBills,
            List<RecentBillDto> recentBills,
            List<DailySalesDto> dailySales
    ) {
        this.totalSales = totalSales;
        this.todaySales = todaySales;
        this.totalBills = totalBills;
        this.recentBills = recentBills;
        this.dailySales = dailySales;
    }

    public BigDecimal  getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal  totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal  getTodaySales() {
        return todaySales;
    }

    public void setTodaySales(BigDecimal  todaySales) {
        this.todaySales = todaySales;
    }

    public Long getTotalBills() {
        return totalBills;
    }

    public void setTotalBills(Long totalBills) {
        this.totalBills = totalBills;
    }

    public List<RecentBillDto> getRecentBills() {
        return recentBills;
    }

    public void setRecentBills(List<RecentBillDto> recentBills) {
        this.recentBills = recentBills;
    }

    public List<DailySalesDto> getDailySales() {
        return dailySales;
    }

    public void setDailySales(List<DailySalesDto> dailySales) {
        this.dailySales = dailySales;
    }
}