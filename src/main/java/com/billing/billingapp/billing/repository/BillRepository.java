package com.billing.billingapp.billing.repository;

import com.billing.billingapp.billing.entity.Bill;
import com.billing.billingapp.report.dto.SalesReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("""
    select b
    from Bill b
    where b.active = true
      and (:search is null
           or b.billNumber like %:search%)
""")
    Page<Bill> findAllActive(
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
select new com.billing.billingapp.report.dto.SalesReportDto(
    b.billNumber,
    b.billDate,
    b.customerName,
    b.total,
    b.status
)
from Bill b
where b.active = true
and b.billDate between :fromDate and :toDate
order by b.billDate desc
""")
    List<SalesReportDto> getSalesReport(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

}
