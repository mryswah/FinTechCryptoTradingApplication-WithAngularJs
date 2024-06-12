package com.fintech.cryptotrading.repository;

import com.fintech.cryptotrading.model.User;
import com.fintech.cryptotrading.model.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {
    @Query("SELECT c from UserWallet c WHERE c.user=:user")
    UserWallet findByUsername(User user);
}
