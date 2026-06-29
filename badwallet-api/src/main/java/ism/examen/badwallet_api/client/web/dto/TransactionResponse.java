package ism.examen.badwallet_api.client.web.dto;

import java.math.BigDecimal;

public record TransactionResponse(
        String type,
        BigDecimal amount,
        String description,
        String timestamp
) {
}
