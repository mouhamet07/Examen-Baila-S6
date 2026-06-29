package ism.examen.payment_service.controller;

import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @PostMapping
    public ResponseEntity<String> pay(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok("Payment processed for " + request.serviceName() + " amount " + request.amount());
    }

    public record PaymentRequest(
            String serviceName,
            BigDecimal amount
    ) {
    }
}
