package com.fintech.cryptotrading.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = "user")
@Table(name = "transactions",
        indexes = {
                @Index(name = "idx_transactionReferenceNumber_user_id_coinName", columnList = "TXNREFNO, user_id, coinName"),
                @Index(name = "idx_status", columnList = "status")
        })
public class Transactions {
    @Id
    @Column(name = "TXNREFNO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_sequence")
    @SequenceGenerator(name = "transaction_sequence", sequenceName = "transaction_sequence", initialValue = 1000, allocationSize = 1)
    private Long transactionReferenceNumber;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String coinName;
    private String orderType;
    private String status;
    private float units;
    private float entryPrice;
    private float currentPrice;
    private float amountPaidFor;
    private float amountSoldFor;
    private float profitLoss;
    @CreationTimestamp
    private LocalDateTime transactionDate;
    @UpdateTimestamp
    private LocalDateTime updatedOn;
}
