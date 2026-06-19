package com.varunkumar.payment_ledger.controller;

import com.varunkumar.payment_ledger.dto.TransferRequest;
import com.varunkumar.payment_ledger.entity.Wallet;
import com.varunkumar.payment_ledger.entity.LedgerEntry;
import com.varunkumar.payment_ledger.exception.InsufficientBalanceException;
import com.varunkumar.payment_ledger.exception.WalletNotFoundException;
import com.varunkumar.payment_ledger.repository.UserRepository;
import com.varunkumar.payment_ledger.repository.WalletRepository;
import com.varunkumar.payment_ledger.repository.LedgerEntryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @GetMapping("/balance")
    public ResponseEntity<?> getMyBalance(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .flatMap(user -> walletRepository.findByUser_Id(user.getId()))
                .map(wallet -> ResponseEntity.ok(wallet.getBalance()))
                .orElse(ResponseEntity.notFound().build());
    }

    // 2. History
    @GetMapping("/history")
    public ResponseEntity<List<LedgerEntry>> getTransactionHistory() {
        return ResponseEntity.ok(ledgerEntryRepository.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable Long userId) {
        return walletRepository.findByUser_Id(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ledger-status")
    public ResponseEntity<?> getLedgerStatus() {
        List<LedgerEntry> entries = ledgerEntryRepository.findAll();

        double totalCredits = entries.stream()
                .filter(e -> "CREDIT".equals(e.getType()))
                .mapToDouble(e -> e.getAmount().doubleValue())
                .sum();

        double totalDebits = entries.stream()
                .filter(e -> "DEBIT".equals(e.getType()))
                .mapToDouble(e -> e.getAmount().doubleValue())
                .sum();

        Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalCredits", totalCredits);
        summary.put("totalDebits", totalDebits);
        summary.put("balanced", Math.abs(totalCredits - totalDebits) < 0.01);

        return ResponseEntity.ok(summary);
    }


    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<String> transferMoney(@Valid @RequestBody TransferRequest request) {

        Wallet fromWallet = walletRepository.findByUser_Id(request.getFromUserId())  // ← fix
                .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found"));

        Wallet toWallet = walletRepository.findByUser_Id(request.getToUserId())  // ← fix
                .orElseThrow(() -> new WalletNotFoundException("Receiver wallet not found"));

        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance!");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
        toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        LedgerEntry debit = new LedgerEntry(fromWallet.getId(), request.getAmount(), "DEBIT");
        ledgerEntryRepository.save(debit);

        LedgerEntry credit = new LedgerEntry(toWallet.getId(), request.getAmount(), "CREDIT");
        ledgerEntryRepository.save(credit);

        return ResponseEntity.ok("Transfer successful!");
    }
}