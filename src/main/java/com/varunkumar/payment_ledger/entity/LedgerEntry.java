package com.varunkumar.payment_ledger.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entry")
@Data
@NoArgsConstructor
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    @JsonProperty("senderName")
    private String senderName;

    @Transient
    @JsonProperty("receiverName")
    private String receiverName;

    private Long walletId;
    private BigDecimal amount;
    private String type;
    private LocalDateTime timestamp;

    @Column(name = "sender_id")
    @JsonProperty("senderId")
    private Long senderId;

    @Column(name = "receiver_id")
    @JsonProperty("receiverId")
    private Long receiverId;

    public LedgerEntry(Long walletId, BigDecimal amount, String type, Long senderId, Long receiverId) {
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = LocalDateTime.now();
    }
}