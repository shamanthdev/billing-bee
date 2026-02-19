package com.billing.billingapp.payment.repository;

import com.billing.billingapp.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBillId(Long billId);

    boolean existsByBillId(Long billId);
}
