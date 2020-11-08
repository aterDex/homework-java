package ru.otus.homework.hw32.back;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class AppBack implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AppBack.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {
            String line = scanner.nextLine();
            if ("exit".equals(line)) {
                return;
            }
        }
    }
}
