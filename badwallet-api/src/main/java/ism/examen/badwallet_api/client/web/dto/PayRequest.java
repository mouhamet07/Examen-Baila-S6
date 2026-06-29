package ism.examen.badwallet_api.client.web.dto;

import java.math.BigDecimal;

public record PayRequest(
        String phoneNumber,
        String serviceName,
        BigDecimal amount
) {
}
