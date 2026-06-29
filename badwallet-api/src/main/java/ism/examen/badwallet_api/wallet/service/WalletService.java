package ism.examen.badwallet_api.wallet.service;

import ism.examen.badwallet_api.client.web.dto.CreateWalletRequest;
import ism.examen.badwallet_api.client.web.dto.TransactionResponse;
import ism.examen.badwallet_api.wallet.data.entity.Wallet;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletService {

    void seedWallets(int numWallets, int eventsPerWallet);
    void createWallet(CreateWalletRequest request);
    Page<Wallet> getWallets(Pageable pageable);
    Wallet getWalletByPhoneNumber(String phoneNumber);
    BigDecimal getWalletBalanceByPhoneNumber(String phoneNumber);
    Wallet deposit(Long walletId, BigDecimal amount, String paymentMethod);
    Wallet withdraw(String phoneNumber, BigDecimal amount);
    Wallet transfer(String senderPhone, String receiverPhone, BigDecimal amount);
    Wallet pay(String phoneNumber, String serviceName, BigDecimal amount);
    Wallet payFactures(String phoneNumber, String serviceName, List<String> factureReferences);
    List<TransactionResponse> getTransactionsByPhoneNumber(String phoneNumber);
}
