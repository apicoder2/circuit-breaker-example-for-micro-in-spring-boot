package com.paymentdemo.controller;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final Random random = new Random();

    @GetMapping("/status")
    public String getPaymentStatus() {
        // 30% failure rate for testing
        if (random.nextInt(10) < 3) {
            throw new RuntimeException("Random payment failure!");
        }
        return "Payment Successful at: " + LocalDateTime.now();
    }
}