package ism.examen.badwallet_api.client.web.dto;

import java.math.BigDecimal;

public record TransferRequest(
        String senderPhone,
        String receiverPhone,
        BigDecimal amount
) {
}
