package com.varunkumar.payment_ledger.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class Wallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    private BigDecimal balance;

    @Version
    private Long version;
}