package com.billing.billingapp.payment.controller;

import com.billing.billingapp.payment.dto.PaymentRequestDto;
import com.billing.billingapp.payment.dto.PaymentResponseDto;
import com.billing.billingapp.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ---------------- CREATE PAYMENT ----------------
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @Valid @RequestBody PaymentRequestDto request
    ) {
        PaymentResponseDto response = paymentService.createPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ---------------- GET PAYMENT BY BILL ID ----------------
    @GetMapping("/bill/{billId}")
    public ResponseEntity<PaymentResponseDto> getPaymentByBillId(
            @PathVariable Long billId
    ) {
        PaymentResponseDto response = paymentService.getPaymentByBillId(billId);
        return ResponseEntity.ok(response);
    }
}
