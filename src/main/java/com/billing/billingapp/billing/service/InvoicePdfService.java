package com.billing.billingapp.billing.service;

import com.billing.billingapp.billing.entity.Bill;
import com.billing.billingapp.billing.entity.BillItem;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class InvoicePdfService {

    public byte[] generateBillPdf(Bill bill) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            // ---------- Title ----------
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            document.add(new Paragraph("Billing Bee - Invoice", titleFont));
            document.add(new Paragraph(" "));

            // ---------- Bill Info ----------
            document.add(new Paragraph("Bill No: " + bill.getBillNumber()));
            document.add(new Paragraph("Date: " + bill.getBillDate()));
            document.add(new Paragraph(" "));

            // ---------- Table ----------
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell("Product");
            table.addCell("Price");
            table.addCell("Qty");
            table.addCell("Total");

            for (BillItem item : bill.getItems()) {
                table.addCell(item.getProductName());
                table.addCell("₹" + item.getPrice());
                table.addCell(item.getQuantity().toString());
                table.addCell("₹" + item.getLineTotal());
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // ---------- Totals ----------
            document.add(new Paragraph("Subtotal: ₹" + bill.getSubtotal()));
            document.add(new Paragraph("Total: ₹" + bill.getTotal()));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating invoice PDF", e);
        }
    }
}
