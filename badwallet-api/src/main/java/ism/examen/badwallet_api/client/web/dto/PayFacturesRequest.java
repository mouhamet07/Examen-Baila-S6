package ism.examen.badwallet_api.client.web.dto;

import java.util.List;

public record PayFacturesRequest(
        String phoneNumber,
        String serviceName,
        List<String> factureReferences
) {
}
