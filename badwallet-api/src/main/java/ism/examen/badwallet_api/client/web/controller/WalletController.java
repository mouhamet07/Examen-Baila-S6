package ism.examen.badwallet_api.client.web.controller;

import ism.examen.badwallet_api.client.web.dto.CreateWalletRequest;
import ism.examen.badwallet_api.shared.response.PagedResponse;
import ism.examen.badwallet_api.shared.response.RestResponse;
import ism.examen.badwallet_api.wallet.data.entity.Wallet;
import ism.examen.badwallet_api.wallet.service.WalletService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<RestResponse<String>> seedWallets(
            @RequestParam int numWallets,
            @RequestParam int eventsPerWallet
    ) {
        walletService.seedWallets(numWallets, eventsPerWallet);
        return ResponseEntity.ok(RestResponse.success(numWallets + " wallets genere avec succes.", null));
    }

    @PostMapping
    public ResponseEntity<RestResponse<String>> createWallet(@RequestBody CreateWalletRequest request) {
        walletService.createWallet(request);
        return ResponseEntity.ok(RestResponse.success("Wallet cree avec succes.", null));
    }

    @GetMapping
    public ResponseEntity<RestResponse<PagedResponse<Wallet>>> getWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Wallet> wallets = walletService.getWallets(PageRequest.of(page, size));
        PagedResponse<Wallet> response = PagedResponse.fromPage(wallets);
        return ResponseEntity.ok(RestResponse.success("Wallets recuperes avec succes.", response));
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<RestResponse<Wallet>> getWalletByPhoneNumber(@PathVariable String phoneNumber) {
        Wallet wallet = walletService.getWalletByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(RestResponse.success("Wallet recupere avec succes.", wallet));
    }

    @GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<RestResponse<BigDecimal>> getWalletBalanceByPhoneNumber(@PathVariable String phoneNumber) {
        BigDecimal balance = walletService.getWalletBalanceByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(RestResponse.success("Solde recupere avec succes.", balance));
    }
}
