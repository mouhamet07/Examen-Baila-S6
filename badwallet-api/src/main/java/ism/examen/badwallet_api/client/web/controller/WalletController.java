package ism.examen.badwallet_api.client.web.controller;

import ism.examen.badwallet_api.client.web.dto.CreateWalletRequest;
import ism.examen.badwallet_api.client.web.dto.DepositRequest;
import ism.examen.badwallet_api.client.web.dto.InvoiceResponse;
import ism.examen.badwallet_api.client.web.dto.PayFacturesRequest;
import ism.examen.badwallet_api.client.web.dto.PayRequest;
import ism.examen.badwallet_api.client.web.dto.TransactionResponse;
import ism.examen.badwallet_api.client.web.dto.TransferRequest;
import ism.examen.badwallet_api.client.web.dto.WithdrawRequest;
import ism.examen.badwallet_api.shared.response.PagedResponse;
import ism.examen.badwallet_api.shared.response.RestResponse;
import ism.examen.badwallet_api.wallet.data.entity.Wallet;
import ism.examen.badwallet_api.wallet.service.WalletService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallets/seed")
    public ResponseEntity<RestResponse<String>> seedWallets(
            @RequestParam int numWallets,
            @RequestParam int eventsPerWallet
    ) {
        walletService.seedWallets(numWallets, eventsPerWallet);
        return ResponseEntity.ok(RestResponse.success(numWallets + " wallets genere avec succes.", null));
    }

    @PostMapping("/wallets")
    public ResponseEntity<RestResponse<String>> createWallet(@RequestBody CreateWalletRequest request) {
        walletService.createWallet(request);
        return ResponseEntity.ok(RestResponse.success("Wallet cree avec succes.", null));
    }

    @GetMapping("/wallets")
    public ResponseEntity<RestResponse<PagedResponse<Wallet>>> getWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Wallet> wallets = walletService.getWallets(PageRequest.of(page, size));
        PagedResponse<Wallet> response = PagedResponse.fromPage(wallets);
        return ResponseEntity.ok(RestResponse.success("Wallets recuperes avec succes.", response));
    }

    @GetMapping("/wallets/{phoneNumber}")
    public ResponseEntity<RestResponse<Wallet>> getWalletByPhoneNumber(@PathVariable String phoneNumber) {
        Wallet wallet = walletService.getWalletByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(RestResponse.success("Wallet recupere avec succes.", wallet));
    }

    @GetMapping("/wallets/{phoneNumber}/balance")
    public ResponseEntity<RestResponse<BigDecimal>> getWalletBalanceByPhoneNumber(@PathVariable String phoneNumber) {
        BigDecimal balance = walletService.getWalletBalanceByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(RestResponse.success("Solde recupere avec succes.", balance));
    }

    @PostMapping("/wallets/{walletId}/deposit")
    public ResponseEntity<RestResponse<String>> deposit(
            @PathVariable Long walletId,
            @RequestBody DepositRequest request
    ) {
        Wallet updatedWallet = walletService.deposit(walletId, request.amount(), request.paymentMethod());
        return ResponseEntity.ok(RestResponse.success("Dépôt effectué avec succès. Nouveau solde : " + updatedWallet.getBalance(), null));
    }

    @PostMapping("/wallets/withdraw")
    public ResponseEntity<RestResponse<String>> withdraw(@RequestBody WithdrawRequest request) {
        Wallet updatedWallet = walletService.withdraw(request.phoneNumber(), request.amount());
        return ResponseEntity.ok(RestResponse.success("Retrait effectué avec succès. Nouveau solde : " + updatedWallet.getBalance(), null));
    }

    @PostMapping("/wallets/transfer")
    public ResponseEntity<RestResponse<String>> transfer(@RequestBody TransferRequest request) {
        Wallet receiverWallet = walletService.transfer(request.senderPhone(), request.receiverPhone(), request.amount());
        return ResponseEntity.ok(RestResponse.success("Transfert effectué avec succès. Nouveau solde du receveur : " + receiverWallet.getBalance(), null));
    }

    @PostMapping("/wallets/pay")
    public ResponseEntity<RestResponse<String>> pay(@RequestBody PayRequest request) {
        Wallet updatedWallet = walletService.pay(request.phoneNumber(), request.serviceName(), request.amount());
        return ResponseEntity.ok(RestResponse.success("Paiement effectué avec succès. Nouveau solde : " + updatedWallet.getBalance(), null));
    }

    @PostMapping("/wallets/pay-factures")
    public ResponseEntity<RestResponse<String>> payFactures(@RequestBody PayFacturesRequest request) {
        Wallet updatedWallet = walletService.payFactures(request.phoneNumber(), request.serviceName(), request.factureReferences());
        return ResponseEntity.ok(RestResponse.success("Paiement des factures effectué avec succès. Nouveau solde : " + updatedWallet.getBalance(), null));
    }

    @GetMapping("/wallets/{phoneNumber}/transactions")
    public ResponseEntity<RestResponse<List<TransactionResponse>>> getTransactionsByPhoneNumber(@PathVariable String phoneNumber) {
        List<TransactionResponse> transactions = walletService.getTransactionsByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(RestResponse.success("Historique des transactions recuperé avec succès.", transactions));
    }

    @GetMapping("/external/factures/{code}/current")
    public ResponseEntity<RestResponse<List<InvoiceResponse>>> getCurrentInvoices(
            @PathVariable String code,
            @RequestParam(required = false) String unite
    ) {
        List<InvoiceResponse> invoices = walletService.getCurrentInvoices(code, unite);
        return ResponseEntity.ok(RestResponse.success("Factures impayées récupérées avec succès.", invoices));
    }

    @GetMapping("/external/factures/{code}/periode")
    public ResponseEntity<RestResponse<List<InvoiceResponse>>> getInvoicesByPeriod(
            @PathVariable String code,
            @RequestParam String debut,
            @RequestParam String fin
    ) {
        List<InvoiceResponse> invoices = walletService.getInvoicesByPeriod(code, LocalDate.parse(debut), LocalDate.parse(fin));
        return ResponseEntity.ok(RestResponse.success("Factures récupérées sur la période demandée.", invoices));
    }
}
