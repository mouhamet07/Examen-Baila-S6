package ism.examen.badwallet_api.wallet.service;

import ism.examen.badwallet_api.client.web.dto.CreateWalletRequest;
import ism.examen.badwallet_api.wallet.data.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletService {

    void seedWallets(int numWallets, int eventsPerWallet);

    void createWallet(CreateWalletRequest request);

    Page<Wallet> getWallets(Pageable pageable);
}
