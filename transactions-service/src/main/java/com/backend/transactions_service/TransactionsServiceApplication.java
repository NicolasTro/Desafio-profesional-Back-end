package com.backend.transactions_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.backend.transactions_service.client")
public class TransactionsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionsServiceApplication.class, args);
    }
}
