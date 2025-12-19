package com.example.backenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendAApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendAApplication.class, args);
        System.out.println("Backend A started - gRPC Server should be running on port 9090");
    }
}
