package ism.examen.badwallet_api.client.web.dto;

import java.math.BigDecimal;

public record CreateWalletRequest(
        String phoneNumber,
        String email,
        BigDecimal initialBalance,
        String code,
        String currency
) {
}
