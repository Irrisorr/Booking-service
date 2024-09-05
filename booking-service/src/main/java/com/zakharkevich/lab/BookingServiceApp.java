package com.zakharkevich.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApp.class, args);
    }
}