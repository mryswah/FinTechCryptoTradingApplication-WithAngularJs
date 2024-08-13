package com.fintech.cryptotrading.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = "user")
@Table(name = "user_crypto_wallet",
        indexes = {
                @Index(name = "idx_user_id_symbol", columnList = "user_id, symbol")
        })
public class UserCryptoWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    private String symbol;
    private float balance;
}
