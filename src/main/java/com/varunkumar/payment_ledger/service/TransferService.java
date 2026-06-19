package com.varunkumar.payment_ledger.service;

import com.varunkumar.payment_ledger.entity.Wallet;
import com.varunkumar.payment_ledger.entity.LedgerEntry;
import com.varunkumar.payment_ledger.repository.LedgerEntryRepository;
import com.varunkumar.payment_ledger.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public void transferFunds(Long senderId, Long receiverId, Double amount) {
        Wallet sender = walletRepository.findByIdForUpdate(senderId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiver = walletRepository.findByIdForUpdate(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (sender.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        sender.setBalance(sender.getBalance().subtract(BigDecimal.valueOf(amount)));
        receiver.setBalance(receiver.getBalance().add(BigDecimal.valueOf(amount)));

        walletRepository.save(sender);
        walletRepository.save(receiver);

        LedgerEntry debit = new LedgerEntry(senderId, BigDecimal.valueOf(amount), "DEBIT");
        LedgerEntry credit = new LedgerEntry(receiverId, BigDecimal.valueOf(amount), "CREDIT");

        ledgerEntryRepository.save(debit);
        ledgerEntryRepository.save(credit);
    }
}