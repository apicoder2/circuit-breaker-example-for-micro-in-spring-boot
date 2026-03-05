package com.productdemo.controller;

import com.productdemo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/check-payment")
    public String checkPayment() {
        return orderService.checkPaymentStatus();
    }
}














//package com.productdemo.controller;
//
//import com.productdemo.service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/orders")
//public class OrderController {
//
//    @Autowired
//    private OrderService orderService;
//
//    @GetMapping("/check-payment")
//    public String checkPayment() {
//        try {
//            return orderService.checkPaymentStatus();
//        } catch (Exception e) {
//            e.printStackTrace(); // This will show in console
//            return "❌ Controller Error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
//        }
//    }
//}