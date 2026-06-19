package com.varunkumar.payment_ledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long walletId;
    private BigDecimal amount;
    private String type;
    private LocalDateTime timestamp = LocalDateTime.now();

    public LedgerEntry(Long walletId, BigDecimal amount, String type) {
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
}