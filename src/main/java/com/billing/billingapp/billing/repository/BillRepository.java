package com.billing.billingapp.billing.repository;

import com.billing.billingapp.billing.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
