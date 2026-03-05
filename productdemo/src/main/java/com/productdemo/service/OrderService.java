package com.productdemo.service;

import com.productdemo.client.PaymentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private PaymentClient paymentClient;

    public String checkPaymentStatus() {
        System.out.println("Calling payment service via Eureka...");
        return paymentClient.getPaymentStatus();  // Feign handles circuit breaker
    }
}