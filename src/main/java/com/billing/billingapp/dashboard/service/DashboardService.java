package com.billing.billingapp.dashboard.service;

import com.billing.billingapp.billing.repository.BillRepository;
import com.billing.billingapp.dashboard.dto.*;
import com.billing.billingapp.dashboard.repository.DashboardRepository;
import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final ProductRepository productRepository;
    private final BillRepository billRepository;

    public DashboardService(DashboardRepository dashboardRepository,
                            ProductRepository productRepository,
                            BillRepository billRepository) {
        this.dashboardRepository = dashboardRepository;
        this.productRepository = productRepository;
        this.billRepository = billRepository;
    }

    /* ==================== SALES DASHBOARD ==================== */
    public SalesDashboardResponseDto getSalesDashboard() {

        BigDecimal totalSales = safeBigDecimal(dashboardRepository.getTotalSales());
        BigDecimal todaySales = safeBigDecimal(dashboardRepository.getTodaySales());
        Long totalBills = dashboardRepository.getTotalBills() != null
                ? dashboardRepository.getTotalBills()
                : 0L;

        List<RecentBillDto> recentBills = dashboardRepository.getRecentBills();
        List<DailySalesDto> dailySales = dashboardRepository.getDailySales();

        return new SalesDashboardResponseDto(
                totalSales,
                todaySales,
                totalBills,
                recentBills,
                dailySales
        );
    }

    /* ==================== LOW STOCK ==================== */
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts(5);
    }

    /* ==================== SIMPLE DASHBOARD ==================== */
    public DashboardDto getDashboard() {

        BigDecimal totalReceived = safeBigDecimal(billRepository.getTotalReceived());
        BigDecimal totalPending = safeBigDecimal(billRepository.getTotalPending());

        return new DashboardDto(totalReceived, totalPending);
    }

    /* ==================== HELPER ==================== */
    private BigDecimal safeBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}