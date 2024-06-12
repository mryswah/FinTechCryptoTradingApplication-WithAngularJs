package com.fintech.cryptotrading.repository;

import com.fintech.cryptotrading.model.CoinPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoinPriceRepository extends JpaRepository<CoinPrice, Long> {
    @Query("SELECT c from CoinPrice c WHERE c.symbol=:query")
    CoinPrice findByName(String query);
}
