package com.billing.billingapp.dashboard.repository;

import com.billing.billingapp.billing.entity.Bill;
import com.billing.billingapp.dashboard.dto.DailySalesDto;
import com.billing.billingapp.dashboard.dto.RecentBillDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Bill, Long> {

    @Query(value = "SELECT SUM(total) FROM bills", nativeQuery = true)
    BigDecimal getTotalSales();

    @Query(value = "SELECT SUM(total) FROM bills WHERE DATE(bill_date) = CURRENT_DATE", nativeQuery = true)
    BigDecimal  getTodaySales();

    @Query(value = "SELECT COUNT(*) FROM bills", nativeQuery = true)
    Long getTotalBills();

    @Query(value = """
        SELECT id,
               bill_number as billNumber,
               bill_date as billDate,
               total as totalAmount
        FROM bills
        ORDER BY bill_date DESC
        LIMIT 5
        """, nativeQuery = true)
    List<RecentBillDto> getRecentBills();

    @Query(value = """
            SELECT DATE(bill_date) as date,
                   SUM(total) as sales
            FROM bills
            GROUP BY DATE(bill_date)
            ORDER BY DATE(bill_date) DESC
            LIMIT 7
            """, nativeQuery = true)
    List<DailySalesDto> getDailySales();
}