package com.varunkumar.payment_ledger.service;

import com.varunkumar.payment_ledger.entity.Wallet;
import com.varunkumar.payment_ledger.entity.LedgerEntry;
import com.varunkumar.payment_ledger.repository.LedgerEntryRepository;
import com.varunkumar.payment_ledger.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;


    @Transactional
    public void transferFunds(Long senderUserId, Long receiverUserId, Double amount) {
        Wallet senderWallet = walletRepository.findByUser_Id(senderUserId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Wallet receiverWallet = walletRepository.findByUser_Id(receiverUserId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        senderWallet.setBalance(senderWallet.getBalance().subtract(BigDecimal.valueOf(amount)));
        receiverWallet.setBalance(receiverWallet.getBalance().add(BigDecimal.valueOf(amount)));
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        LedgerEntry debit = new LedgerEntry();
        debit.setWalletId(senderWallet.getId());
        debit.setSenderId(senderUserId);
        debit.setReceiverId(receiverUserId);
        debit.setAmount(BigDecimal.valueOf(amount));
        debit.setType("DEBIT");
        debit.setTimestamp(LocalDateTime.now());
        ledgerEntryRepository.save(debit);

        LedgerEntry credit = new LedgerEntry();
        credit.setWalletId(receiverWallet.getId());
        credit.setSenderId(senderUserId);
        credit.setReceiverId(receiverUserId);
        credit.setAmount(BigDecimal.valueOf(amount));
        credit.setType("CREDIT");
        credit.setTimestamp(LocalDateTime.now());
        ledgerEntryRepository.save(credit);
    }
}