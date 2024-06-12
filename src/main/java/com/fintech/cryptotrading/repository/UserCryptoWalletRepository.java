package com.fintech.cryptotrading.repository;

import com.fintech.cryptotrading.model.User;
import com.fintech.cryptotrading.model.UserCryptoWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserCryptoWalletRepository extends JpaRepository<UserCryptoWallet, Long> {
    @Query("SELECT c from UserCryptoWallet c WHERE c.user=:user AND c.symbol=:symbol")
    UserCryptoWallet findByUserAndSymbol(User user, String symbol);
}
