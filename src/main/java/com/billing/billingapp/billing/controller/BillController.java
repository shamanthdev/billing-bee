package com.billing.billingapp.billing.controller;

import com.billing.billingapp.billing.dto.BillListResponseDto;
import com.billing.billingapp.billing.dto.BillResponseDto;
import com.billing.billingapp.billing.dto.CreateBillRequestDto;
import com.billing.billingapp.billing.repository.BillRepository;
import com.billing.billingapp.billing.service.BillService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.billing.billingapp.billing.entity.Bill;
import com.billing.billingapp.billing.service.InvoicePdfService;
import com.billing.billingapp.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;
    private final InvoicePdfService invoicePdfService;
    private final BillRepository billRepository;

    public BillController(BillService billService, InvoicePdfService invoicePdfService, BillRepository billRepository) {
        this.billService = billService;
        this.invoicePdfService = invoicePdfService;
        this.billRepository = billRepository;
    }

    @PostMapping
    public ResponseEntity<Long> createBill(
            @RequestBody CreateBillRequestDto dto
    ) {
        Long billId = billService.createBill(dto);
        return ResponseEntity.ok(billId);
    }

    @GetMapping("/ping")
    public String ping() {
        return "Bills API working";
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<BillResponseDto> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }



    @GetMapping("/list")
    public Page<BillListResponseDto> getBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return billService.getBills(page, size, search);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadBillPdf(@PathVariable Long id) throws Exception {

        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

        byte[] pdf = invoicePdfService.generateBillPdf(bill);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + bill.getBillNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBill(@PathVariable Long id) {
        billService.cancelBill(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{billId}")
    public ResponseEntity<Void> updateBill(
            @PathVariable Long billId,
            @RequestBody CreateBillRequestDto dto
    ) {
        billService.updateBill(billId, dto);
        return ResponseEntity.ok().build();
    }


}

