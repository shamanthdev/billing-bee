package com.billing.billingapp.report.service;

import com.billing.billingapp.report.dto.SalesReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    List<SalesReportDto> getSalesReport(
            LocalDateTime fromDate,
            LocalDateTime toDate
    );
}