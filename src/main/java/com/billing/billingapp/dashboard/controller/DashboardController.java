package com.billing.billingapp.dashboard.controller;

import com.billing.billingapp.dashboard.dto.SalesDashboardResponseDto;
import com.billing.billingapp.dashboard.service.DashboardService;
import com.billing.billingapp.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/sales")
    public SalesDashboardResponseDto getSalesDashboard() {
        return dashboardService.getSalesDashboard();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        return ResponseEntity.ok(dashboardService.getLowStockProducts());
    }
}