package com.fintech.cryptotrading.repository;

import com.fintech.cryptotrading.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    @Query("SELECT c from Transactions c WHERE c.transactionReferenceNumber=:transactionReferenceNumber AND c.user.id=:userId AND c.coinName=:symbol")
    Transactions findByQueries(Long transactionReferenceNumber, Long userId, String symbol);
    @Query("SELECT c from Transactions c WHERE c.status=:query")
    List<Transactions> findAllOpen(String query);
}
