package ru.otus.homework.hw32.middle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class AppMiddle implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(AppMiddle.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("start-example")) {
            log.info("Start examples");
            log.info("======={}", args.getOptionValues("work-directory"));
            String java = args.getOptionValues("java").get(0);
            String workDirectory = args.getOptionValues("work-directory").get(0);
            String front = args.getOptionValues("front").get(0);
            String back = args.getOptionValues("back").get(0);
            Thread.sleep(1000);
            Process second = new ProcessBuilder(java, "-jar", back)
                    .directory(new File(workDirectory))
                    .redirectOutput(new File("second.log"))
                    .redirectErrorStream(true)
                    .start();
            Thread.sleep(1000);
            Process first = new ProcessBuilder(java, "-jar", front)
                    .directory(new File(workDirectory))
                    .redirectOutput(new File("first.log"))
                    .redirectErrorStream(true)
                    .start();

            log.info("front pid {}", first.pid());
            log.info("second pid {}", second.pid());
        }
    }
}
