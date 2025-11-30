package com.stationery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // <-- IMPORTANT

@SpringBootApplication
@EnableAsync // Enable asynchronous method execution (for EmailService)
public class StationeryApplication {

    public static void main(String[] args) {
        SpringApplication.run(StationeryApplication.class, args);
    }

}