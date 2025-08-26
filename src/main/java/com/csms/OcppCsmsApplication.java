package com.csms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OcppCsmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OcppCsmsApplication.class, args);
    }
}