package ism.examen.badwallet_api.wallet.service;

import ism.examen.badwallet_api.client.web.dto.CreateWalletRequest;

public interface WalletService {

    void seedWallets(int numWallets, int eventsPerWallet);

    void createWallet(CreateWalletRequest request);
}
