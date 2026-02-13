package com.billing.billingapp.billing.repository;

import com.billing.billingapp.billing.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
