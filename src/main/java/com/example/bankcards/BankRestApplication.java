package com.example.bankcards;

import com.example.bankcards.util.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The BankRestApplication class is the main class of the program. It is responsible for running the Spring Boot application
 * and initiating the Bank_Rest functionality. The main method starts the Spring application context and ensures that
 * the BankRestApplication class is used as the configuration class. And also loads variables from .env using the DotenvLoader loader.
 */
@SpringBootApplication
public class BankRestApplication {
    public static void main(String[] args) {
        DotenvLoader.load();
        SpringApplication.run(BankRestApplication.class, args);
    }
}
