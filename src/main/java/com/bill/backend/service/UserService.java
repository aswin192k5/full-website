package com.bill.backend.service;

import com.bill.backend.model.User;
import com.bill.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void recharge(String username, double amount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double currentBalance = user.getAvailableBalance() != null
                ? user.getAvailableBalance()
                : 0.0;

        user.setAvailableBalance(currentBalance + amount);
        userRepository.save(user);
    }
}
