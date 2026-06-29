package ism.examen.badwallet_api.wallet.service.impl;

import ism.examen.badwallet_api.client.web.dto.CreateWalletRequest;
import ism.examen.badwallet_api.shared.exception.EntityNotFoundException;
import ism.examen.badwallet_api.wallet.data.entity.Wallet;
import ism.examen.badwallet_api.wallet.data.repository.WalletRepository;
import ism.examen.badwallet_api.wallet.service.WalletService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private static final String CURRENCY = "XOF";
    private static final long MIN_BALANCE = 5_000L;
    private static final long MAX_BALANCE = 500_000L;
    private final WalletRepository walletRepository;
    private final Random random = new Random();
    @Override
    public void seedWallets(int numWallets, int eventsPerWallet) {
        long count = walletRepository.count();
        List<Wallet> wallets = new ArrayList<>();
        long phoneNumber = 221770000000L + count;
        long walletNumber = count + 1;
        for (int i = 0; i < numWallets; i++) {
            phoneNumber = phoneNumber + 1;
            walletNumber = walletNumber + 1;
            String phone = String.valueOf(phoneNumber);
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
    }

    @Override
    public Page<Wallet> getWallets(Pageable pageable) {
        return walletRepository.findAll(pageable);
    }

    @Override
    public Wallet getWalletByPhoneNumber(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("Wallet introuvable."));
    }
}
