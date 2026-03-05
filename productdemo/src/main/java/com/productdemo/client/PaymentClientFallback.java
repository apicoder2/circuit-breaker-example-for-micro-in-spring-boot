package com.productdemo.client;

import org.springframework.stereotype.Component;

@Component
public class PaymentClientFallback implements PaymentClient {

    @Override
    public String getPaymentStatus() {
        return "⚠️ Payment Service is DOWN - Circuit OPEN (Feign Fallback)";
    }
}