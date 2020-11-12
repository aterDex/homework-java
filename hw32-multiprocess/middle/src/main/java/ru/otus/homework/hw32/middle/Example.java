package ru.otus.homework.hw32.middle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;

import java.io.File;

@Slf4j
public class Example {
    public static void go(ApplicationArguments args) throws Exception {
        log.info("Start examples");
        try {
            String java = args.getOptionValues("java").get(0);
            String workDirectory = args.getOptionValues("work-directory").get(0);
            String front = args.getOptionValues("front").get(0);
            String back = args.getOptionValues("back").get(0);

            Process backPr0 = new ProcessBuilder(java, "-jar", back,
                    "--spring.profiles.active=gRPC,H2_DB_server")
                    .directory(new File(workDirectory))
                    .redirectOutput(new File(workDirectory, "back0.log"))
                    .redirectErrorStream(true)
                    .start();
            // Даем время стартонуть первой бд
            Thread.sleep(10000);
            Process backPr1 = new ProcessBuilder(java, "-jar", back,
                    "--message-system.frontend-service-client-name=frontend2",
                    "--message-system.database-service-client-name=back2",
                    "--spring.datasource.url=jdbc:h2:tcp://localhost:9123/mem:OtusExamplesDB",
                    "--spring.profiles.active=SignalTcp"
            )
                    .directory(new File(workDirectory))
                    .redirectOutput(new File(workDirectory, "back1.log"))
                    .redirectErrorStream(true)
                    .start();

            Process frontPr0 = new ProcessBuilder(java, "-jar", front,
                    "--spring.profiles.active=RMI")
                    .directory(new File(workDirectory))
                    .redirectOutput(new File(workDirectory, "front0.log"))
                    .redirectErrorStream(true)
                    .start();

            Process frontPr1 = new ProcessBuilder(java, "-jar", front,
                    "--server.port=8081",
                    "--message-system.frontend-service-client-name=frontend2",
                    "--message-system.database-service-client-name=back2",
                    "--spring.profiles.active=SignalTcp")
                    .directory(new File(workDirectory))
                    .redirectOutput(new File(workDirectory, "front1.log"))
                    .redirectErrorStream(true)
                    .start();

            log.info("front0 pid {}", frontPr0.pid());
            log.info("front1 pid {}", frontPr1.pid());
            log.info("back0 pid {}", backPr0.pid());
            log.info("back1 pid {}", backPr1.pid());
        } catch (Exception e) {
            log.error("Problem with start example.", e);
            System.exit(0);
        }
    }
}

