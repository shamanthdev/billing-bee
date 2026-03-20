package com.billing.billingapp.report.controller;

import com.billing.billingapp.report.dto.SalesReportDto;
import com.billing.billingapp.report.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public List<SalesReportDto> getSalesReport(
            @RequestParam LocalDateTime fromDate,
            @RequestParam LocalDateTime toDate
    ) {
        return reportService.getSalesReport(fromDate, toDate);
    }
}