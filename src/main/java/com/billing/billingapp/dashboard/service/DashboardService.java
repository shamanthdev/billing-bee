package com.billing.billingapp.dashboard.service;

import com.billing.billingapp.dashboard.dto.DailySalesDto;
import com.billing.billingapp.dashboard.dto.RecentBillDto;
import com.billing.billingapp.dashboard.dto.SalesDashboardResponseDto;
import com.billing.billingapp.dashboard.repository.DashboardRepository;
import com.billing.billingapp.product.Product;
import com.billing.billingapp.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final ProductRepository productRepository;

    public DashboardService(DashboardRepository dashboardRepository,
                            ProductRepository productRepository) {
        this.dashboardRepository = dashboardRepository;
        this.productRepository = productRepository;
    }

    public SalesDashboardResponseDto getSalesDashboard() {

        Double totalSales = dashboardRepository.getTotalSales();
        Double todaySales = dashboardRepository.getTodaySales();
        Long totalBills = dashboardRepository.getTotalBills();

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

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts(5);
    }
}