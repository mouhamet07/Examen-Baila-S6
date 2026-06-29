package ism.examen.badwallet_api.wallet.service.impl;

import ism.examen.badwallet_api.client.web.dto.CreateWalletRequest;
import ism.examen.badwallet_api.client.web.dto.TransactionResponse;
import ism.examen.badwallet_api.shared.exception.BadRequestException;
import ism.examen.badwallet_api.shared.exception.EntityNotFoundException;
import ism.examen.badwallet_api.wallet.data.entity.Wallet;
import ism.examen.badwallet_api.wallet.data.repository.WalletRepository;
import ism.examen.badwallet_api.wallet.service.WalletService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private static final String CURRENCY = "XOF";
    private static final String WALLET_NOT_FOUND_MESSAGE = "Wallet introuvable.";
    private static final long MIN_BALANCE = 5_000L;
    private static final long MAX_BALANCE = 500_000L;
    private final WalletRepository walletRepository;
    private final RestClient restClient;
    private final Random random = new Random();
    private final Map<String, List<TransactionResponse>> transactionsByPhone = new ConcurrentHashMap<>();

    public WalletServiceImpl(WalletRepository walletRepository, @Value("${payment.service.url:http://localhost:8081}") String paymentServiceUrl) {
        this.walletRepository = walletRepository;
        this.restClient = RestClient.builder().baseUrl(paymentServiceUrl).build();
    }
    @Override
    public void seedWallets(int numWallets, int eventsPerWallet) {
        long count = walletRepository.count();
        List<Wallet> wallets = new ArrayList<>();
        long phoneNumber = 221770000000L + count;
        long walletNumber = count + 1;
        for (int i = 0; i < numWallets; i++) {
            phoneNumber = phoneNumber + 1;
            walletNumber = walletNumber + 1;
            String phone = "+" + phoneNumber;
            String email = "wallet" + walletNumber + "@gmail.com";
            String code = "WLT-" + walletNumber;
            long min = MIN_BALANCE;
            long max = MAX_BALANCE;
            long balanceValue = random.nextLong(min, max);
            BigDecimal balance = BigDecimal.valueOf(balanceValue);
            Wallet wallet = Wallet.builder()
                    .phoneNumber(phone)
                    .email(email)
                    .code(code)
                    .currency(CURRENCY)
                    .balance(balance)
                    .build();
            wallets.add(wallet);
        }
        walletRepository.saveAll(wallets);
    }

    @Override
    public void createWallet(CreateWalletRequest request) {
        Wallet wallet = Wallet.builder()
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .code(request.code())
                .currency(request.currency())
                .balance(request.initialBalance())
                .build();
        walletRepository.save(wallet);
        addTransaction(request.phoneNumber(), "CREATE", request.initialBalance(), "Création du wallet", Instant.now().toString());
    }

    @Override
    public Page<Wallet> getWallets(Pageable pageable) {
        return walletRepository.findAll(pageable);
    }

    @Override
    public Wallet getWalletByPhoneNumber(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(WALLET_NOT_FOUND_MESSAGE));
    }

    @Override
    public BigDecimal getWalletBalanceByPhoneNumber(String phoneNumber) {
        return getWalletByPhoneNumber(phoneNumber).getBalance();
    }

    @Override
    public Wallet deposit(Long walletId, BigDecimal amount, String paymentMethod) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant du dépôt doit être supérieur à zéro.");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new BadRequestException("La méthode de paiement est obligatoire.");
        }
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException(WALLET_NOT_FOUND_MESSAGE));
        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet savedWallet = walletRepository.save(wallet);
        addTransaction(wallet.getPhoneNumber(), "DEPOSIT", amount, "Dépôt", Instant.now().toString());
        return savedWallet;
    }

    @Override
    public Wallet withdraw(String phoneNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant du retrait doit être supérieur à zéro.");
        }
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(WALLET_NOT_FOUND_MESSAGE));
        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.01)).setScale(2, java.math.RoundingMode.HALF_UP);
        if (fee.compareTo(BigDecimal.valueOf(5000)) > 0) {
            fee = BigDecimal.valueOf(5000);
        }
        BigDecimal totalDebit = amount.add(fee);
        if (wallet.getBalance().compareTo(totalDebit) < 0) {
            throw new BadRequestException("Solde insuffisant pour effectuer ce retrait.");
        }
        wallet.setBalance(wallet.getBalance().subtract(totalDebit));
        Wallet savedWallet = walletRepository.save(wallet);
        addTransaction(wallet.getPhoneNumber(), "WITHDRAW", totalDebit, "Retrait", Instant.now().toString());
        return savedWallet;
    }

    @Override
    public Wallet transfer(String senderPhone, String receiverPhone, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant du transfert doit être supérieur à zéro.");
        }
        if (senderPhone == null || senderPhone.isBlank() || receiverPhone == null || receiverPhone.isBlank()) {
            throw new BadRequestException("Les numéros de téléphone des deux wallets sont obligatoires.");
        }
        if (senderPhone.equals(receiverPhone)) {
            throw new BadRequestException("Le sender et le receiver doivent être différents.");
        }
        Wallet sender = walletRepository.findByPhoneNumber(senderPhone)
                .orElseThrow(() -> new EntityNotFoundException(WALLET_NOT_FOUND_MESSAGE));
        Wallet receiver = walletRepository.findByPhoneNumber(receiverPhone)
                .orElseThrow(() -> new EntityNotFoundException(WALLET_NOT_FOUND_MESSAGE));
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Solde insuffisant pour effectuer ce transfert.");
        }
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        walletRepository.save(sender);
        Wallet savedReceiver = walletRepository.save(receiver);
        addTransaction(sender.getPhoneNumber(), "TRANSFER_OUT", amount, "Transfert vers " + receiverPhone, Instant.now().toString());
        addTransaction(receiver.getPhoneNumber(), "TRANSFER_IN", amount, "Transfert depuis " + senderPhone, Instant.now().toString());
        return savedReceiver;
    }

    @Override
    public Wallet pay(String phoneNumber, String serviceName, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant du paiement doit être supérieur à zéro.");
        }
        if (serviceName == null || serviceName.isBlank()) {
            throw new BadRequestException("Le nom du service est obligatoire.");
        }
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(WALLET_NOT_FOUND_MESSAGE));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Solde insuffisant pour effectuer ce paiement.");
        }

        restClient.post()
                .uri("/api/payments")
                .body(new PaymentRequest(serviceName, amount))
                .retrieve()
                .toBodilessEntity();
        wallet.setBalance(wallet.getBalance().subtract(amount));
        Wallet savedWallet = walletRepository.save(wallet);
        addTransaction(wallet.getPhoneNumber(), "PAYMENT", amount, "Paiement " + serviceName, Instant.now().toString());
        return savedWallet;
    }

    @Override
    public Wallet payFactures(String phoneNumber, String serviceName, List<String> factureReferences) {
        if (factureReferences == null || factureReferences.isEmpty()) {
            throw new BadRequestException("Au moins une facture doit être spécifiée.");
        }
        if (serviceName == null || serviceName.isBlank()) {
            throw new BadRequestException("Le nom du service est obligatoire.");
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (String reference : factureReferences) {
            if (reference == null || reference.isBlank()) {
                continue;
            }
            totalAmount = totalAmount.add(BigDecimal.valueOf(5000));
        }
        return pay(phoneNumber, serviceName, totalAmount);
    }

    @Override
    public List<TransactionResponse> getTransactionsByPhoneNumber(String phoneNumber) {
        return transactionsByPhone.getOrDefault(phoneNumber, new ArrayList<>());
    }

    private void addTransaction(String phoneNumber, String type, BigDecimal amount, String description, String timestamp) {
        transactionsByPhone.computeIfAbsent(phoneNumber, key -> new ArrayList<>())
                .add(new TransactionResponse(type, amount, description, timestamp));
    }

    private record PaymentRequest(String serviceName, BigDecimal amount) {
    }
}
