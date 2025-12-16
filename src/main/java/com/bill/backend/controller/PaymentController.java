package com.bill.backend.controller;

import com.bill.backend.service.PaymentService;
import com.bill.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    // Create Razorpay order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> payload) {
        try {
            double amount = Double.parseDouble(payload.get("amount").toString());
            String currency = payload.getOrDefault("currency", "INR").toString();
            String receipt = "rcpt_" + System.currentTimeMillis();

            return ResponseEntity.ok(
                    paymentService.createOrder(amount, currency, receipt)
            );

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "order_creation_failed", "message", e.getMessage()));
        }
    }

    // Verify + Recharge
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, Object> payload) {
        try {
            String username = payload.get("username").toString();
            double amount = Double.parseDouble(payload.get("amount").toString());

            String orderId = payload.get("razorpay_order_id").toString();
            String paymentId = payload.get("razorpay_payment_id").toString();
            String signature = payload.get("razorpay_signature").toString();

            boolean verified = paymentService.verifySignature(orderId, paymentId, signature);

            if (!verified) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "verification_failed"));
            }

            // ✅ DIRECT recharge (NO localhost)
            userService.recharge(username, amount);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "amount", amount
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "verification_error", "message", e.getMessage()));
        }
    }
}
