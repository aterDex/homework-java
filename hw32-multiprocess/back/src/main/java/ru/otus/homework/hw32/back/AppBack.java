package ru.otus.homework.hw32.back;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Slf4j
@SpringBootApplication
public class AppBack implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AppBack.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            Scanner scanner = new Scanner(System.in);
            while (!Thread.currentThread().isInterrupted()) {
                String line = scanner.nextLine();
                if ("exit".equals(line)) {
                    System.exit(0);
                }
            }
        } catch (NoSuchElementException e) {
            log.error("" , e);
            System.exit(0);
        }
    }
}
