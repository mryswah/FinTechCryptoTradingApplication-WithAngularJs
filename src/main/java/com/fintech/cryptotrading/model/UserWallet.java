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
@Table(name = "user_wallet",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id")
        })
public class UserWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    private String symbol;
    @Column(name = "ACCOUNTBALANCE")
    private float accountBalance;
}
