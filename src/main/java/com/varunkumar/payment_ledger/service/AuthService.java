package com.varunkumar.payment_ledger.service;

import com.varunkumar.payment_ledger.entity.User;
import com.varunkumar.payment_ledger.entity.Wallet;
import com.varunkumar.payment_ledger.repository.UserRepository;
import com.varunkumar.payment_ledger.repository.WalletRepository;
import com.varunkumar.payment_ledger.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public Map<String, Object> authenticate(String username, String password) {
        System.out.println("Authenticating user: " + username);
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (user.getPassword().equals(password)) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("token", jwtUtils.generateToken(username));
                        response.put("userId", user.getId());
                        response.put("username", user.getUsername());
                        return response;
                    } else {
                        throw new RuntimeException("Invalid Password");
                    }
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Map<String, Object> register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        userRepository.save(newUser);

        Wallet wallet = new Wallet();
        wallet.setUser(newUser);
        walletRepository.save(wallet);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtUtils.generateToken(username));
        response.put("userId", newUser.getId());
        response.put("username", newUser.getUsername());
        return response;
    }
}