package ism.examen.badwallet_api.client.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponse(
        String reference,
        String serviceName,
        BigDecimal amount,
        String status,
        String unit,
        LocalDate dueDate
) {
}
