package ism.examen.badwallet_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ism.examen.badwallet_api.client.web.controller.WalletController;
import ism.examen.badwallet_api.client.web.dto.InvoiceResponse;
import ism.examen.badwallet_api.shared.response.RestResponse;
import ism.examen.badwallet_api.wallet.service.WalletService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class WalletControllerTest {

    @Test
    void shouldFilterInvoicesByUnit() {
        WalletService walletService = mock(WalletService.class);
        when(walletService.getCurrentInvoices(any(), any())).thenReturn(List.of(
                new InvoiceResponse("FAC-001", "WOYAFAL", new BigDecimal("5000"), "UNPAID", "WOYAFAL", LocalDate.of(2026, 6, 15))
        ));

        WalletController controller = new WalletController(walletService);
        ResponseEntity<RestResponse<List<InvoiceResponse>>> response = controller.getCurrentInvoices("WLT-0000003", "WOYAFAL");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("WOYAFAL", response.getBody().data().get(0).unit());
    }
}
