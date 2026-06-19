package com.varunkumar.payment_ledger.controller;

import com.varunkumar.payment_ledger.entity.User;
import com.varunkumar.payment_ledger.entity.Wallet;
import com.varunkumar.payment_ledger.repository.UserRepository;
import com.varunkumar.payment_ledger.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @PostMapping("/register")
    @Transactional
    public User registerUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        return savedUser;
    }
}