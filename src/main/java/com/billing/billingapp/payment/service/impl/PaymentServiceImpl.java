package com.billing.billingapp.payment.service.impl;

import com.billing.billingapp.billing.entity.Bill;
import com.billing.billingapp.billing.entity.BillStatus;
import com.billing.billingapp.billing.repository.BillRepository;
import com.billing.billingapp.common.exception.ResourceNotFoundException;
import com.billing.billingapp.payment.dto.PaymentRequestDto;
import com.billing.billingapp.payment.dto.PaymentResponseDto;
import com.billing.billingapp.payment.entity.Payment;
import com.billing.billingapp.payment.repository.PaymentRepository;
import com.billing.billingapp.payment.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BillRepository billRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
    }

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto request) {

        // 1️⃣ Fetch Bill
        Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bill not found with id: " + request.getBillId()
                        )
                );

        // 2️⃣ Validate Bill Status
        if (bill.getStatus() != BillStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Payment not allowed for bill with status: " + bill.getStatus()
            );
        }

        // 3️⃣ Prevent duplicate payment
        if (paymentRepository.existsByBillId(bill.getId())) {
            throw new IllegalStateException("Payment already exists for this bill");
        }

        // 4️⃣ Validate amount
        BigDecimal billTotal = bill.getTotal(); // adjust if your field name differs
        if (billTotal.compareTo(request.getAmount()) != 0) {
            throw new IllegalStateException(
                    "Payment amount must be equal to bill total"
            );
        }

        // 5️⃣ Create Payment entity
        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setPaymentMode(request.getPaymentMode());
        payment.setAmount(request.getAmount());
        payment.setTransactionRef(request.getTransactionRef());

        Payment savedPayment = paymentRepository.save(payment);

        // 6️⃣ Update bill status → PAID
        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);

        // 7️⃣ Map & return response
        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponseDto getPaymentByBillId(Long billId) {

        Payment payment = paymentRepository.findByBillId(billId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for bill id: " + billId
                        )
                );

        return mapToResponse(payment);
    }

    // ---------- Mapper ----------
    private PaymentResponseDto mapToResponse(Payment payment) {

        PaymentResponseDto response = new PaymentResponseDto();
        response.setPaymentId(payment.getId());
        response.setBillId(payment.getBill().getId());
        response.setPaymentMode(payment.getPaymentMode());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setTransactionRef(payment.getTransactionRef());

        return response;
    }
}
