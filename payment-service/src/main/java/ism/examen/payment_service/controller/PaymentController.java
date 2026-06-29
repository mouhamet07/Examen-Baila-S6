package ism.examen.payment_service.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @PostMapping("/payments")
    public ResponseEntity<String> pay(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok("Payment processed for " + request.serviceName() + " amount " + request.amount());
    }

    @GetMapping("/factures/{code}/current")
    public ResponseEntity<List<InvoiceResponse>> getCurrentInvoices(
            @PathVariable String code,
            @RequestParam(required = false) String unite
    ) {
        List<InvoiceResponse> invoices = List.of(
                new InvoiceResponse("FAC-ISM-3-1", code, new BigDecimal("5000.00"), "UNPAID", "WOYAFAL", LocalDate.of(2026, 6, 15)),
                new InvoiceResponse("FAC-ISM-3-3", code, new BigDecimal("5000.00"), "UNPAID", "WOYAFAL", LocalDate.of(2026, 6, 20)),
                new InvoiceResponse("FAC-ISM-3-5", code, new BigDecimal("7500.00"), "UNPAID", "SOGEA", LocalDate.of(2026, 6, 25))
        );
        if (unite != null && !unite.isBlank()) {
            invoices = invoices.stream().filter(invoice -> invoice.unit().equalsIgnoreCase(unite)).toList();
        }
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/factures/{code}/periode")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByPeriod(
            @PathVariable String code,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        List<InvoiceResponse> invoices = List.of(
                new InvoiceResponse("FAC-ISM-3-1", code, new BigDecimal("5000.00"), "UNPAID", "WOYAFAL", LocalDate.of(2026, 5, 15)),
                new InvoiceResponse("FAC-ISM-3-3", code, new BigDecimal("5000.00"), "UNPAID", "WOYAFAL", LocalDate.of(2026, 6, 20)),
                new InvoiceResponse("FAC-ISM-3-5", code, new BigDecimal("7500.00"), "UNPAID", "SOGEA", LocalDate.of(2026, 7, 10))
        );
        invoices = invoices.stream()
                .filter(invoice -> !invoice.dueDate().isBefore(debut) && !invoice.dueDate().isAfter(fin))
                .toList();
        return ResponseEntity.ok(invoices);
    }

    public record PaymentRequest(
            String serviceName,
            BigDecimal amount
    ) {
    }

    public record InvoiceResponse(
            String reference,
            String serviceName,
            BigDecimal amount,
            String status,
            String unit,
            LocalDate dueDate
    ) {
    }
}
