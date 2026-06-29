package ism.examen.badwallet_api.shared.mapper;

import ism.examen.badwallet_api.client.web.dto.WithdrawRequest;
import ism.examen.badwallet_api.wallet.data.entity.Wallet;

public class WalletMapper {

    private WalletMapper() {
    }

    public static Wallet toEntity(WithdrawRequest request) {
        return Wallet.builder()
                .phoneNumber(request.phoneNumber())
                .build();
    }
}
