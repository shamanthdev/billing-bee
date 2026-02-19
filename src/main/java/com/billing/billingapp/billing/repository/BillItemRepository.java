package com.billing.billingapp.billing.repository;

import com.billing.billingapp.billing.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {
}
