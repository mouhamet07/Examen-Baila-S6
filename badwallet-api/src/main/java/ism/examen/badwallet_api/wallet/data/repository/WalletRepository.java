package ism.examen.badwallet_api.wallet.data.repository;

import ism.examen.badwallet_api.wallet.data.entity.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByPhoneNumber(String phoneNumber);
}
