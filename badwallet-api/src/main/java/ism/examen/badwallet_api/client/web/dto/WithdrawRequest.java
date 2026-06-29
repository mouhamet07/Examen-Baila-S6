package ism.examen.badwallet_api.client.web.dto;

import java.math.BigDecimal;

public record WithdrawRequest(
        String phoneNumber,
        BigDecimal amount
) {
}
