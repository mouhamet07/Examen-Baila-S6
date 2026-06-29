package ism.examen.badwallet_api.client.web.dto;

import java.math.BigDecimal;

public record DepositRequest(
        BigDecimal amount,
        String paymentMethod
) {
}
