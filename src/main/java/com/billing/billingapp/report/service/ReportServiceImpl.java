package com.billing.billingapp.report.service;

import com.billing.billingapp.billing.repository.BillRepository;
import com.billing.billingapp.report.dto.SalesReportDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final BillRepository billRepository;

    public ReportServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public List<SalesReportDto> getSalesReport(
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        return billRepository.getSalesReport(fromDate, toDate);
    }
}