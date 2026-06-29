package ism.examen.badwallet_api.client.web.controller;

import ism.examen.badwallet_api.client.web.dto.CreateWalletRequest;
import ism.examen.badwallet_api.shared.response.ApiResponse;
import ism.examen.badwallet_api.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/seed")
    public ResponseEntity<ApiResponse> seedWallets(
            @RequestParam int numWallets,
            @RequestParam int eventsPerWallet
    ) {
        walletService.seedWallets(numWallets, eventsPerWallet);
        return ResponseEntity.ok(new ApiResponse(numWallets + " wallets generé avec succès."));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createWallet(@RequestBody CreateWalletRequest request) {
        walletService.createWallet(request);
        return ResponseEntity.ok(new ApiResponse("Wallet crée avec succès."));
    }
}
