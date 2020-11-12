package ru.otus.homework.hw32.middle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AppMiddle implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(AppMiddle.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("start-example")) {
            Example.go(args);
        }
    }
}
