package com.billing.billingapp.payment.service;

import com.billing.billingapp.payment.dto.PaymentRequestDto;
import com.billing.billingapp.payment.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto request);

    PaymentResponseDto getPaymentByBillId(Long billId);
}
